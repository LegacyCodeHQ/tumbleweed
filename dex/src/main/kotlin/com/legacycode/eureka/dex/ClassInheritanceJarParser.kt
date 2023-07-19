package com.legacycode.eureka.dex

import java.io.File
import java.io.FileInputStream
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import net.bytebuddy.jar.asm.ClassReader

class ClassInheritanceJarParser(override val file: File) : ArtifactParser {
  companion object {
    private const val CLASS_FILE_EXTENSION = ".class"
    private const val REGEX_ANONYMOUS_INNER_CLASS_SUFFIX = ".+\\$\\d$"
  }

  override fun buildAdjacencyList(): InheritanceAdjacencyList {
    val adjacencyList = InheritanceAdjacencyList()

    file.inputStream().use { inputStream ->
      parseClassFiles(inputStream, adjacencyList)
    }

    return adjacencyList
  }

  private fun parseClassFiles(
    inputStream: FileInputStream,
    outAdjacencyList: InheritanceAdjacencyList,
  ) {
    val zipInputStream = ZipInputStream(inputStream)
    var entry = zipInputStream.nextEntry
    while (entry != null) {
      if (isClassFile(entry)) {
        val classBytes = zipInputStream.readBytes()
        val classReader = ClassReader(classBytes)
        if (!isAnonymousInnerClass(classReader.className)) {
          outAdjacencyList.add(Ancestor("L${classReader.superName};"), Child("L${classReader.className};"))
        }
      }
      entry = zipInputStream.nextEntry
    }
  }

  private fun isClassFile(entry: ZipEntry): Boolean {
    return !entry.isDirectory && entry.name.endsWith(CLASS_FILE_EXTENSION)
  }

  private fun isAnonymousInnerClass(className: String?): Boolean {
    return Pattern.matches(REGEX_ANONYMOUS_INNER_CLASS_SUFFIX, className)
  }
}