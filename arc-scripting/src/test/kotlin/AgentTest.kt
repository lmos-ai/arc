// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting

import ai.ancf.lmos.arc.agents.ChatAgent
import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.conversation.toConversation
import ai.ancf.lmos.arc.core.Success
import ai.ancf.lmos.arc.core.getOrThrow
import io.mockk.coEvery
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AgentTest : TestBase() {

    @Test
    fun `test creating simple agent`(): Unit = runBlocking {
        eval("weather.agent.kts")
        assertThat(testContext.agents).hasSize(1)
    }

    @Test
    fun `test agent execution`(): Unit = runBlocking {
        val input = slot<List<ConversationMessage>>()
        coEvery { chatCompleter.complete(capture(input)) } answers { Success(AssistantMessage("answer")) }

        testBeanProvider.setContext(setOf(chatCompleterProvider)) {
            val agent = eval("weather.agent.kts") as ChatAgent
            agent.execute("A question".toConversation(User("test"))).getOrThrow()
        }

        assertThat(input.captured.size).isEqualTo(2)
    }
}
