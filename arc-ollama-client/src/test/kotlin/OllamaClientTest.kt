// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.client.ollama

import io.github.lmos.arc.agents.llm.TextEmbeddings
import io.github.lmos.arc.agents.llm.embed
import io.github.lmos.arc.agents.llm.similarity
import io.github.lmos.arc.core.getOrThrow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class OllamaClientTest {

    @Test
    fun `test text`(): Unit = runBlocking {
        val client = OllamaClient(OllamaClientConfig(modelName = "llama3:8b", url = "http://localhost:11434"))
        val t1 = client.embed("Hello, world!").getOrThrow().embedding
        val t2 = client.embed("Hello, world!").getOrThrow().embedding
        val similarity = similarity(t1, t2)
       // assertThat(similarity).isEqualTo(1.0000000000000002)

        val l = listOf(
            "Wie hoch ist mein Kontostand?",
            "Wie viel Geld habe ich auf meinem Konto?",
            "Was ist mein Kontostand?",
            "Wie kann ich mein Router neustarten?",
            "Was ist MagentaTV?",
            "How much does MagentaTV cost?",
            "Kontostand?",
        ).map {
            client.embed(it).getOrThrow()
        }
        val t = TextEmbeddings(l)
        val look = client.embed("Was kostet TV").getOrThrow()

        val result = t.findClosest(look)
        println(result)
    }
}