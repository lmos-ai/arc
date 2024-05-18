// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.client.gemini

import com.google.cloud.vertexai.VertexAI
import com.google.cloud.vertexai.api.Content
import com.google.cloud.vertexai.api.FunctionDeclaration
import com.google.cloud.vertexai.api.GenerateContentResponse
import com.google.cloud.vertexai.api.GenerationConfig
import com.google.cloud.vertexai.api.Schema
import com.google.cloud.vertexai.api.Tool
import com.google.cloud.vertexai.api.Type
import com.google.cloud.vertexai.generativeai.ContentMaker.forRole
import com.google.cloud.vertexai.generativeai.GenerativeModel
import com.google.cloud.vertexai.generativeai.ResponseHandler
import io.github.lmos.arc.agents.ArcException
import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.ConversationMessage
import io.github.lmos.arc.agents.conversation.SystemMessage
import io.github.lmos.arc.agents.conversation.UserMessage
import io.github.lmos.arc.agents.events.EventPublisher
import io.github.lmos.arc.agents.functions.LLMFunction
import io.github.lmos.arc.agents.llm.ChatCompleter
import io.github.lmos.arc.agents.llm.ChatCompletionSettings
import io.github.lmos.arc.agents.llm.LLMFinishedEvent
import io.github.lmos.arc.agents.llm.LLMStartedEvent
import io.github.lmos.arc.client.gemini.ByteMapper.map
import io.github.lmos.arc.core.Result
import io.github.lmos.arc.core.Success
import io.github.lmos.arc.core.failWith
import io.github.lmos.arc.core.finally
import io.github.lmos.arc.core.getOrThrow
import io.github.lmos.arc.core.result
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Calls a Gemini endpoint to complete a conversation.
 */
class GeminiClient(
    private val languageModel: GeminiClientConfig,
    private val vertexAi: VertexAI,
    private val eventHandler: EventPublisher? = null,
) : ChatCompleter {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun complete(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ) = result<AssistantMessage, ArcException> {
        val vertexMessages = toVertexMessage(messages.systemToUser()) // gemini does not support system message
        val vertexFunctions = functions?.let { toVertexFunctions(it) }
        val functionCallHandler = FunctionCallHandler(functions ?: emptyList(), eventHandler)

        eventHandler?.publish(LLMStartedEvent(languageModel.modelName))

        val result: Result<GenerateContentResponse, ArcException>
        val duration = measureTime {
            result = chat(vertexMessages, vertexFunctions, functionCallHandler, settings)
        }

        var response: GenerateContentResponse? = null
        finally { publishEvent(it, response, duration, functionCallHandler) }
        response = result failWith { ArcException("Failed to call Gemini!", it) }
        AssistantMessage(ResponseHandler.getText(response), sensitive = functionCallHandler.calledSensitiveFunction())
    }

    private fun publishEvent(
        result: Result<AssistantMessage, ArcException>,
        response: GenerateContentResponse?,
        duration: Duration,
        functionCallHandler: FunctionCallHandler,
    ) {
        eventHandler?.publish(
            LLMFinishedEvent(
                result,
                languageModel.modelName,
                totalTokens = response?.usageMetadata?.totalTokenCount ?: -1,
                promptTokens = response?.usageMetadata?.promptTokenCount ?: -1,
                completionTokens = response?.usageMetadata?.candidatesTokenCount ?: -1,
                functionCallHandler.calledFunctions.size,
                duration,
            ),
        )
    }

    private suspend fun chat(
        messages: List<Content>,
        functions: List<Tool>?,
        functionCallHandler: FunctionCallHandler,
        settings: ChatCompletionSettings?,
    ): Result<GenerateContentResponse, ArcException> {
        val generationConfig = GenerationConfig.newBuilder()
            .apply { settings?.maxTokens?.let { maxOutputTokens = it } }
            .apply { settings?.temperature?.let { temperature = it.toFloat() } }
            .apply { settings?.topP?.let { topP = it.toFloat() } }
            .build()
        val model = GenerativeModel.Builder()
            .setModelName(languageModel.modelName)
            .setVertexAi(vertexAi)
            .setGenerationConfig(generationConfig)
            .apply { if (functions != null) setTools(functions) }
            .build()
        // TODO use async method
        val response = model.generateContent(messages)

        val newMessages = functionCallHandler.handle(response).getOrThrow()
        if (newMessages.isNotEmpty()) {
            return chat(messages + newMessages, functions, functionCallHandler, settings)
        }
        return Success(response)
    }

    fun toVertexFunctions(functions: List<LLMFunction>) = functions.map { function ->
        Tool.newBuilder().addFunctionDeclarations(
            FunctionDeclaration.newBuilder()
                .setName(function.name)
                .setDescription(function.description)
                .apply {
                    if (function.parameters.parameters.isNotEmpty()) {
                        setParameters(
                            Schema.newBuilder()
                                .setType(Type.OBJECT)
                                .apply {
                                    putAllProperties(
                                        function.parameters.parameters.associate { param ->
                                            // TODO add missing settings
                                            param.name to Schema.newBuilder()
                                                .setType(Type.STRING)
                                                .setDescription(param.description)
                                                .build()
                                        },
                                    )
                                },
                        )
                    }
                }
                .build(),
        ).build()
    }

    /**
     * Combines system messages with user messages.
     * This expects a single system message at the beginning of the list followed by a user message.
     */
    private fun List<ConversationMessage>.systemToUser(): List<ConversationMessage> {
        if (size < 2 || first() !is SystemMessage) return this
        val systemMessage = get(0)
        val userMessage = get(1)
        val combinedUserMessage = userMessage.update("${systemMessage.content}\n\n${userMessage.content}")
        return listOf(combinedUserMessage) + drop(2)
    }

    private fun toVertexMessage(messages: List<ConversationMessage>) =
        messages.mapNotNull {
            when (it) {
                // TODO
                is UserMessage -> if (it.binaryData.isNotEmpty()) {
                    forRole("user").fromMultiModalData(
                        it.content,
                        *it.binaryData.map { map(it.mimeType, it.data) }.toTypedArray(),
                    )
                } else {
                    forRole("user").fromString(it.content)
                }

                is AssistantMessage -> forRole("model").fromString(it.content)
                is SystemMessage -> null // gemini does not support system message
            }
        }
}
