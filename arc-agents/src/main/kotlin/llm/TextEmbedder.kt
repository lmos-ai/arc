// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.llm

import io.github.lmos.arc.agents.ArcException
import io.github.lmos.arc.core.Result
import io.github.lmos.arc.core.failWith
import io.github.lmos.arc.core.result

/**
 * Creates text embeddings for a given text.
 */
interface TextEmbedder {
    suspend fun embed(texts: List<String>): Result<TextEmbeddings, ArcException>
}

suspend fun TextEmbedder.embed(text: String) = result<TextEmbedding, ArcException> {
    embed(listOf(text)).failWith { it }.embeddings.first()
}

data class TextEmbedding(val text: String, val embedding: List<Double>)

/**
 * A collection of text embeddings.
 */
class TextEmbeddings(val embeddings: List<TextEmbedding>) {

    fun findSimilar(text: TextEmbedding, similarity: Double): List<TextEmbedding> {
        return embeddings.filter { similarity(it.embedding, text.embedding) >= similarity }
    }

    fun findClosest(text: TextEmbedding): Pair<TextEmbedding, Double> {
        var currentSimilarity = -1.0
        var currentTextEmbedding = embeddings.first()
        embeddings.forEach {
            val s = similarity(it.embedding, text.embedding)
            println(s to it.text)
            if (s >= currentSimilarity) {
                currentSimilarity = s
                currentTextEmbedding = it
            }
        }
        return currentTextEmbedding to currentSimilarity
    }
}