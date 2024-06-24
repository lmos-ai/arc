// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.spring.ai

import io.github.lmos.arc.agents.llm.complete
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class SpringChatClientTest : TestBase() {

    @Test
    fun `test chat completion text`(): Unit = runBlocking {
        val result = springChatClient.complete("test question")
        println(result)
    }
}
