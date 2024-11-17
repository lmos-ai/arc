// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents

import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.conversation.toConversation
import ai.ancf.lmos.arc.agents.dsl.*
import ai.ancf.lmos.arc.agents.functions.LLMFunction
import ai.ancf.lmos.arc.agents.functions.LLMFunctionException
import ai.ancf.lmos.arc.agents.functions.LLMFunctionProvider
import ai.ancf.lmos.arc.agents.functions.ParametersSchema
import ai.ancf.lmos.arc.agents.llm.ChatCompleter
import ai.ancf.lmos.arc.agents.llm.ChatCompleterProvider
import ai.ancf.lmos.arc.core.Result
import ai.ancf.lmos.arc.core.Success
import ai.ancf.lmos.arc.core.getOrThrow
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

class TestFunction(override val name: String = "test") : LLMFunction {
    override val parameters = ParametersSchema(emptyList(), emptyList())
    override val description: String = "test"
    override val group: String? = null
    override val isSensitive: Boolean = false
    override suspend fun execute(input: Map<String, Any?>): Result<String, LLMFunctionException> {
        return Success("test")
    }
}
