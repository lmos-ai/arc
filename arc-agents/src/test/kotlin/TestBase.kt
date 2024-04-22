// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents

import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.Conversation
import io.github.lmos.arc.agents.conversation.ConversationMessage
import io.github.lmos.arc.agents.conversation.toConversation
import io.github.lmos.arc.agents.dsl.AgentDefinition
import io.github.lmos.arc.agents.dsl.BasicAgentDefinitionContext
import io.github.lmos.arc.agents.dsl.ChatAgentFactory
import io.github.lmos.arc.agents.dsl.CoroutineBeanProvider
import io.github.lmos.arc.agents.dsl.DateFilter
import io.github.lmos.arc.agents.dsl.NumberFilter
import io.github.lmos.arc.agents.functions.LLMFunctionProvider
import io.github.lmos.arc.agents.llm.ChatCompleter
import io.github.lmos.arc.agents.llm.ChatCompleterProvider
import io.github.lmos.arc.core.Success
import io.github.lmos.arc.core.getOrThrow
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach

open class TestBase {

    val testBeanProvider = CoroutineBeanProvider()
    val testAgentFactory = ChatAgentFactory(testBeanProvider)
    val testContext = BasicAgentDefinitionContext(testAgentFactory)
    val chatCompleter = mockk<ChatCompleter>()
    val chatCompleterProvider = ChatCompleterProvider { chatCompleter }
    val functionProvider: LLMFunctionProvider = mockk<LLMFunctionProvider>()
    val contextBeans =
        setOf(chatCompleterProvider, functionProvider, DateFilter(), NumberFilter())

    fun agent(agent: AgentDefinition.() -> Unit): Agent<*, *> {
        val context = BasicAgentDefinitionContext(testAgentFactory)
        context.agent(agent)
        return context.agents.first()
    }

    suspend fun executeAgent(
        agent: ChatAgent,
        message: String,
        llmResponse: String = "answer",
        context: Set<Any>? = null,
    ): Pair<List<ConversationMessage>, Conversation> {
        val input = slot<List<ConversationMessage>>()
        coEvery { chatCompleter.complete(capture(input)) } answers { Success(AssistantMessage(llmResponse)) }
        coEvery { chatCompleter.complete(capture(input), any()) } answers { Success(AssistantMessage(llmResponse)) }

        var result: Conversation
        testBeanProvider.setContext(context ?: contextBeans) {
            result = agent.execute(message.toConversation(User("user"))).getOrThrow()
        }
        return input.captured to result
    }

    @BeforeEach
    fun cleanMocks() {
        clearMocks(chatCompleter, functionProvider)
    }
}
