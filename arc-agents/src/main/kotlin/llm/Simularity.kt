package io.github.lmos.arc.agents.llm

import kotlin.math.sqrt

fun similarity(vec1: List<Double>, vec2: List<Double>): Double {
    require(vec1.size == vec2.size) { "Vector sizes must be equal!" }
    val dotProduct = vec1.zip(vec2) { a, b -> a * b }.sum()
    val magnitude1 = sqrt(vec1.sumOf { it * it })
    val magnitude2 = sqrt(vec2.sumOf { it * it })
    return if (magnitude1 == 0.0 || magnitude2 == 0.0) {
        0.0
    } else {
        dotProduct / (magnitude1 * magnitude2)
    }
}
