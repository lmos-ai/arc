// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting

import ai.ancf.lmos.arc.agents.ArcException
import ai.ancf.lmos.arc.agents.ChatAgent
import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.*
import ai.ancf.lmos.arc.agents.functions.LLMFunction
import ai.ancf.lmos.arc.agents.llm.ChatCompleter
import ai.ancf.lmos.arc.agents.llm.ChatCompleterProvider
import ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings
import ai.ancf.lmos.arc.core.Result
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

    @Test
    fun `test agent execution with one time loading from init block`(): Unit = runBlocking {
        val assistantMessage: String
        assistantMessage = testBeanProvider.setContext(
            setOf(
                ChatCompleterProvider {
                    object : ChatCompleter {
                        override suspend fun complete(
                            messages: List<ConversationMessage>,
                            functions: List<LLMFunction>?,
                            settings: ChatCompletionSettings?,
                        ): Result<AssistantMessage, ArcException> {
                            return messages.findLast { it is SystemMessage }?.let {
                                Success(AssistantMessage(it.content))
                            } ?: throw ArcException("No system message found")
                        }
                    }
                },
            ),
        ) {
            val agent = eval("kb-summarizer.agent.kts") as ChatAgent
            // execute twice.
            agent.execute("A question".toConversation(User("test")))
                .getOrThrow().transcript.findLast { it is AssistantMessage }?.content ?: "No assistant message found"
            agent.execute("A question".toConversation(User("test")))
                .getOrThrow().transcript.findLast { it is AssistantMessage }?.content ?: "No assistant message found"
        }

        assertThat(assistantMessage).contains("This is a file to test init block of arc agents.")
        assertThat(assistantMessage).contains("Number of times file loaded = 1")
    }
}
