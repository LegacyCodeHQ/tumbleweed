package com.legacycode.eureka.cli.hierarchy

import com.legacycode.eureka.dex.Ancestor
import com.legacycode.eureka.dex.ApkParser
import com.legacycode.eureka.dex.ArtifactParser
import com.legacycode.eureka.dex.JvmArtifactParser
import com.legacycode.eureka.hierarchy.HierarchyServer
import java.io.File
import java.util.Locale
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

@Command(
  name = "hierarchy",
  description = ["prints the inheritance hierarchy of the specified class"],
)
class HierarchyCommand : Runnable {
  companion object {
    private const val DEFAULT_PORT = 7090
    private const val APK_EXTENSION = "apk"
  }

  @Parameters(
    index = "0",
    description = ["path to the APK file"],
    arity = "1",
  )
  private lateinit var apkFile: File

  @Option(
    names = ["-n", "--name"],
    description = ["fully qualified class name of the root class"],
    required = true,
  )
  private lateinit var rootClassName: String

  override fun run() {
    val adjacencyList = getParser(apkFile).inheritanceAdjacencyList()
    val rootClassDescriptor = toClassDescriptor(rootClassName)
    val root = Ancestor(rootClassDescriptor)

    if (adjacencyList.children(root).isNotEmpty()) {
      HierarchyServer(adjacencyList, root, apkFile).start(DEFAULT_PORT)
    } else {
      println("Oops… '$rootClassName' does not have an inheritance hierarchy")
    }
  }

  private fun getParser(artifactFile: File): ArtifactParser {
    return if (artifactFile.extension.lowercase(Locale.ENGLISH) == APK_EXTENSION) {
      ApkParser(artifactFile)
    } else {
      JvmArtifactParser(artifactFile)
    }
  }

  private fun toClassDescriptor(className: String): String {
    return "L${className.replace('.', '/')};"
  }
}
