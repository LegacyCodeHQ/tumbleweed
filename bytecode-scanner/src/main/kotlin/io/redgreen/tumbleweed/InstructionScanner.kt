package io.redgreen.tumbleweed

import net.bytebuddy.jar.asm.Handle
import net.bytebuddy.jar.asm.MethodVisitor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

typealias Opcode = Int

object InstructionScanner {
  private val logger: Logger = LoggerFactory.getLogger(InstructionScanner::class.java)

  fun scan(
    topLevelType: QualifiedType,
    caller: Method,
    constantPool: ConstantPool,
    outRelationships: MutableList<Relationship>,
  ): MethodVisitor {
    return object : MethodVisitor(ASM_API_VERSION) {
      private var loadedConstant: Any? = null

      override fun visitFieldInsn(
        opcode: Opcode,
        owner: String,
        fieldName: String,
        fieldDescriptor: String,
      ) {
        logger.debug("Visiting field instruction ({}): {}/{}", opcode.instruction, owner, fieldName)

        val field = Field(fieldName, FieldDescriptor.from(fieldDescriptor), QualifiedType(owner))
        val relationship = Relationship(caller, field, Relationship.Type.from(opcode))
        if (owner == topLevelType.name) {
          logger.debug("Adding relationship: {}", relationship)
          outRelationships.add(relationship)
        } else {
          logger.debug("Skipping relationship: {}", relationship)
        }
      }

      override fun visitMethodInsn(
        opcode: Opcode,
        owner: String,
        methodName: String,
        methodDescriptor: String,
        isInterface: Boolean,
      ) {
        logger.debug("Visiting method instruction ({}): {}/{}", opcode.instruction, owner, methodName)

        if (topLevelType.name.startsWith(owner) || owner.startsWith("${topLevelType.name}\$")) {
          val callee = Method(methodName, MethodDescriptor(methodDescriptor), QualifiedType(owner))
          val relationship = Relationship(caller, callee, Relationship.Type.from(opcode))

          logger.debug("Adding relationship: {}", relationship)
          outRelationships.add(relationship)
        } else {
          logger.debug("Skipping method instruction ({}): {}/{}", opcode.instruction, owner, methodName)
        }

        if (loadedConstant != null) {
          val field = constantPool[loadedConstant]
          if (field != null) {
            val relationship = Relationship(caller, field, Relationship.Type.Reads)

            logger.debug("Adding relationship: {}", relationship)
            outRelationships.add(relationship)
          }
        }
      }

      override fun visitInvokeDynamicInsn(
        name: String?,
        descriptor: String?,
        bootstrapMethodHandle: Handle?,
        vararg bootstrapMethodArguments: Any?,
      ) {
        logger.debug("Visiting invoke dynamic instruction: {}, {}", name, bootstrapMethodArguments.contentToString())

        if (bootstrapMethodArguments.size > 1) {
          val methodHandle = bootstrapMethodArguments[1] as Handle
          val callee = Method(methodHandle.name, MethodDescriptor(methodHandle.desc), topLevelType)
          outRelationships.add(Relationship(caller, callee, Relationship.Type.Calls))
        } else {
          logger.debug("Ignoring dynamic instruction: {}{}", name, descriptor)
        }
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
      }

      override fun visitLdcInsn(value: Any?) {
        logger.debug("Visiting ldc instruction: {}", value)

        loadedConstant = value
        super.visitLdcInsn(value)
      }
    }
  }

  @Suppress("MagicNumber")
  private val Opcode.instruction: String
    get() {
      return when (this) {
        0xb2 -> "getstatic"
        0xb3 -> "putstatic"
        0xb5 -> "putfield"
        0xb4 -> "getfield"
        0xb7 -> "invokespecial"
        0xb6 -> "invokevirtual"
        0xb8 -> "invokestatic"
        0xb9 -> "invokeinterface"
        else -> "unmapped ($this)"
      }
    }
}
