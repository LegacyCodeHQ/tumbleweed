package com.legacycode.eureka.dex

@JvmInline
value class Descriptor private constructor(val name: String) {
  companion object {
    private const val DIRECTIVE_EXACT = "exact:"

    fun from(name: String): Descriptor {
      return Descriptor(name)
    }
  }

  private val isInnerClass: Boolean
    get() = simpleClassName.contains('$')

  private val simpleClassName: String
    get() = name.substring(name.lastIndexOf('/') + 1).dropLast(1)

  fun matches(searchTerm: String): Boolean {
    val makeExactMatch = searchTerm.startsWith(DIRECTIVE_EXACT, true)
    if (makeExactMatch) {
      val maybeWordsWithHyphenation = simpleClassName.split("(?<=.)(?=\\p{Lu})".toRegex())
      val words = maybeWordsWithHyphenation.flatMap { it.split("_", "-") }
      val keyword = searchTerm.substring(DIRECTIVE_EXACT.length)

      return words.any { word -> word.equals(keyword, true) }
    }

    if (isInnerClass) {
      val nestedSimpleClassName = simpleClassName.substring(simpleClassName.lastIndexOf('$') + 1)
      return nestedSimpleClassName.contains(searchTerm, true)
    }

    return simpleClassName.contains(searchTerm, true)
  }
}