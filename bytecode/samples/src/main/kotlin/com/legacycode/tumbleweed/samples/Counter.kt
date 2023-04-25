@file:Suppress("unused")

package com.legacycode.tumbleweed.samples

data class Counter(val count: Int = 0) {
  fun increment(): Counter = copy(count = count + 1)
}