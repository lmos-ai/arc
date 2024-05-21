// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.router

import io.github.lmos.arc.agents.ArcException
import io.github.lmos.arc.agents.llm.TextEmbedder
import io.github.lmos.arc.agents.llm.TextEmbedding
import io.github.lmos.arc.agents.llm.TextEmbeddings
import io.github.lmos.arc.core.result
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RouterTest {

    val textEmbedder = object : TextEmbedder {
        override suspend fun embed(text: List<String>) = result<TextEmbeddings, ArcException> {
            TextEmbeddings(text.map { TextEmbedding(it, listOf(0.0, 0.1, it.hashCode().toDouble())) })
        }
    }

    @Test
    fun `test route correctly routes requests`(): Unit = runBlocking {
        val subject = SemanticRouter(textEmbedder, null)
        subject.addRoute("destination1".routeBy("route1", "route2"))
        subject.addRoute("destination2".routeBy("route3", "route4"))

        assertThat(subject.route("route4")?.destination).isEqualTo("destination2")
    }
}
