// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents

import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.SystemMessage
import ai.ancf.lmos.arc.agents.dsl.BasicDSLContext
import ai.ancf.lmos.arc.agents.dsl.BeanProvider
import ai.ancf.lmos.arc.agents.dsl.CoroutineBeanProvider
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.InputFilterContext
import ai.ancf.lmos.arc.agents.dsl.OutputFilterContext
import ai.ancf.lmos.arc.agents.dsl.provideOptional
import ai.ancf.lmos.arc.agents.events.EventPublisher
import ai.ancf.lmos.arc.agents.functions.LLMFunctionProvider
import ai.ancf.lmos.arc.agents.llm.ChatCompleterProvider
import ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings
import ai.ancf.lmos.arc.core.Result
import ai.ancf.lmos.arc.core.failWith
import ai.ancf.lmos.arc.core.getOrThrow
import ai.ancf.lmos.arc.core.mapFailure
import ai.ancf.lmos.arc.core.result
import kotlin.time.measureTime

const val AGENT_LOG_CONTEXT_KEY = "agent"

/**
 * A ChatAgent is an Agent that can interact with a user in a chat-like manner.
 */
class ChatAgent(
    override val name: String,
    override val description: String,
    private val model: () -> String?,
    private val settings: () -> ChatCompletionSettings?,
    private val beanProvider: BeanProvider,
    private val systemPrompt: suspend DSLContext.() -> String,
    private val tools: List<String>,
    private val filterOutput: suspend OutputFilterContext.() -> Unit,
    private val filterInput: suspend InputFilterContext.() -> Unit,
) : Agent<Conversation, Conversation> {

    override suspend fun execute(input: Conversation, context: Set<Any>): Result<Conversation, AgentFailedException> {
        return withLogContext(mapOf(AGENT_LOG_CONTEXT_KEY to name)) {
            val agentEventHandler = beanProvider.provideOptional<EventPublisher>()
            val model = model()

            agentEventHandler?.publish(AgentStartedEvent(this@ChatAgent))
            val result: Result<Conversation, AgentFailedException>
            val duration = measureTime {
                result = doExecute(input, model, context).mapFailure { AgentFailedException("Agent $name failed!", it) }
            }
            agentEventHandler?.publish(
                AgentFinishedEvent(
                    this@ChatAgent,
                    input = input,
                    output = result,
                    model = model,
                    duration = duration,
                ),
            )
            result
        }
    }

    private suspend fun doExecute(conversation: Conversation, model: String?, context: Set<Any>) =
        result<Conversation, Exception> {
            val scriptingContext = BasicDSLContext(beanProvider)
            val chatCompleter = chatCompleter(model = model)
            val functions = functions()

            CoroutineBeanProvider().setContext(context + setOf(conversation, conversation.user)) {
                val generatedSystemPrompt = systemPrompt.invoke(scriptingContext)
                val filterContext = InputFilterContext(scriptingContext, conversation)
                val filteredInput = filterInput.invoke(filterContext).let { filterContext.input }

                if (filteredInput.isEmpty()) failWith { AgentNotExecutedException("Input has been filtered") }

                val fullConversation =
                    listOf(SystemMessage(generatedSystemPrompt)) + filteredInput.transcript
                val completedConversation =
                    conversation + chatCompleter.complete(fullConversation, functions, settings()).getOrThrow()

                val filterOutputContext =
                    OutputFilterContext(scriptingContext, conversation, completedConversation, generatedSystemPrompt)
                filterOutput.invoke(filterOutputContext).let { filterOutputContext.output }
            }
        }

    private suspend fun chatCompleter(model: String?) =
        beanProvider.provide(ChatCompleterProvider::class).provideByModel(model = model)

    private suspend fun functions() = if (tools.isNotEmpty()) {
        val functionProvider = beanProvider.provide(LLMFunctionProvider::class)
        tools.map { functionProvider.provide(it).getOrThrow() }
    } else {
        null
    }

    override fun toString(): String {
        return "ChatAgent(name='$name', description='$description')"
    }
}
