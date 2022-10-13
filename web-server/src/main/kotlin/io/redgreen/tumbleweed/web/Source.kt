package io.redgreen.tumbleweed.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.redgreen.tumbleweed.ClassScanner
import io.redgreen.tumbleweed.web.observablehq.BilevelEdgeBundlingGraph
import io.redgreen.tumbleweed.web.observablehq.graph
import java.io.File

sealed interface Source {
  val location: File
  val graph: BilevelEdgeBundlingGraph
}

class CompiledClassFile(override val location: File) : Source {
  override val graph: BilevelEdgeBundlingGraph
    get() = ClassScanner.scan(location).graph
}

class JsonFile(override val location: File) : Source {
  override val graph: BilevelEdgeBundlingGraph
    get() = jacksonObjectMapper()
      .readValue(location.readText(), BilevelEdgeBundlingGraph::class.java)
}
