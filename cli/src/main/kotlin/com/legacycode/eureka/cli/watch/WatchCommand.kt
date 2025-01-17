package com.legacycode.eureka.cli.watch

import com.legacycode.eureka.android.AndroidMemberClassifier
import com.legacycode.eureka.cli.DEFAULT_PORT
import com.legacycode.eureka.filesystem.CompiledClassFileFinder
import com.legacycode.eureka.viz.edgebundling.BasicMemberClassifier
import com.legacycode.eureka.viz.edgebundling.CompiledClassFile
import com.legacycode.eureka.web.watch.Experiment
import com.legacycode.eureka.web.watch.Experiment.android
import com.legacycode.eureka.web.watch.WatchServer
import java.io.File
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

@Command(
  name = "watch",
  description = ["visualize real-time JVM class structure in your browser"],
)
class WatchCommand : Runnable {
  @Parameters(
    index = "0",
    description = ["uniquely identifiable (partially or fully) qualified class name"],
    arity = "1",
  )
  lateinit var className: String

  @Option(
    names = ["-b", "--buildDir"],
    description = ["path to the build directory"],
    required = false,
  )
  var buildDir: File? = null

  @Option(
    names = ["-p", "--port"],
    description = ["the server port number"],
    defaultValue = "$DEFAULT_PORT",
    required = false,
  )
  var port: Int = DEFAULT_PORT

  @Option(
    names = ["-x", "--experiment"],
    description = ["available features: ${'$'}{COMPLETION-CANDIDATES}"],
    required = false,
  )
  private var experiment: Experiment? = null

  override fun run() {
    val classFilePath = CompiledClassFileFinder
      .find(className, (buildDir ?: File("")).absolutePath)

    if (classFilePath == null) {
      printClassNotFoundMessage(className)
      return
    }

    val classifier = if (experiment == android) {
      AndroidMemberClassifier()
    } else {
      BasicMemberClassifier()
    }

    WatchServer(experiment).start(CompiledClassFile(classFilePath.toFile(), classifier), port)
  }

  private fun printClassNotFoundMessage(className: String) {
    println("Sorry, but the class '${className}' could not be found.")
    println("  • Ensure that you have built the project before running eureka.")
    println("  • If your Kotlin source file does not contain a top-level class, try using '${className}Kt' instead.")
  }
}
