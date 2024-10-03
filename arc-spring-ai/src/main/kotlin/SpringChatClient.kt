// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package ai.ancf.lmos.arc.spring.ai

import ai.ancf.lmos.arc.agents.ArcException
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.events.EventPublisher
import ai.ancf.lmos.arc.agents.functions.LLMFunction
import ai.ancf.lmos.arc.agents.llm.ChatCompleter
import ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings
import ai.ancf.lmos.arc.agents.llm.LLMFinishedEvent
import ai.ancf.lmos.arc.agents.llm.LLMStartedEvent
import ai.ancf.lmos.arc.core.Result
import ai.ancf.lmos.arc.core.failWith
import ai.ancf.lmos.arc.core.mapFailure
import ai.ancf.lmos.arc.core.result
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.ChatOptionsBuilder
import org.springframework.ai.chat.prompt.Prompt
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Implements the ChatCompleter interface using the Spring AI ChatModel.
 */
open class SpringChatClient(
    protected val client: ChatModel,
    private val modelName: String,
    private val eventHandler: EventPublisher? = null,
) : ChatCompleter {

    /**
     * Calls the LLM.
     */
    protected open fun call(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ): Result<ChatResponse, Exception> = result<ChatResponse, Exception> {
        val prompt = Prompt(messages.toSpringAI(), settings.toSpringAI())
        client.call(prompt)
    }

    override suspend fun complete(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ) = result<AssistantMessage, ArcException> {
        eventHandler?.publish(LLMStartedEvent(modelName))

        var chatResponse: ChatResponse? = null
        val result: Result<AssistantMessage, ArcException>
        val duration = measureTime {
            result = result<AssistantMessage, Exception> {
                chatResponse = call(messages, functions, settings) failWith { it }
                AssistantMessage(chatResponse!!.result.output.content)
            }.mapFailure { ArcException(it.message ?: "Unknown exception!", it) }
        }

        publishEvent(result, messages, functions, chatResponse, duration, settings)
        result failWith { it }
    }

    /**
     * Converts ChatCompletionSettings to Spring AI ChatOptions.
     */
    private fun ChatCompletionSettings?.toSpringAI() = ChatOptionsBuilder.builder().apply {
        this@toSpringAI?.temperature?.let { withTemperature(it) }
        this@toSpringAI?.topP?.let { withTopP(it) }
        this@toSpringAI?.topK?.let { withTopK(it) }
    }.build()

    /**
     * Publishes the LLMFinishedEvent.
     */
    private fun publishEvent(
        result: Result<AssistantMessage, ArcException>,
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        chatResponse: ChatResponse?,
        duration: Duration,
        settings: ChatCompletionSettings?,
    ) {
        eventHandler?.publish(
            LLMFinishedEvent(
                result,
                messages,
                functions,
                modelName,
                chatResponse?.metadata?.usage?.totalTokens?.toInt() ?: -1,
                chatResponse?.metadata?.usage?.promptTokens?.toInt() ?: -1,
                chatResponse?.metadata?.usage?.generationTokens?.toInt() ?: -1,
                0,
                duration,
                settings = settings,
            ),
        )
    }
}
