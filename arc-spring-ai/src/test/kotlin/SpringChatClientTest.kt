// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.spring.ai

import ai.ancf.lmos.arc.agents.llm.complete
import ai.ancf.lmos.arc.core.getOrNull
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

class SpringChatClientTest : TestBase() {

    // @Test
    fun `test chat completion text`(): Unit = runBlocking {
        val result = springChatClient.complete("test question")
        assertThat(result.getOrNull()?.content).isEqualTo("answer to test")
    }
}
