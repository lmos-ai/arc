// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.client.azure

import com.azure.ai.openai.models.ChatCompletions
import com.azure.ai.openai.models.ChatCompletionsFunctionToolCall
import com.azure.ai.openai.models.ChatRequestAssistantMessage
import com.azure.ai.openai.models.ChatRequestMessage
import com.azure.ai.openai.models.ChatRequestToolMessage
import com.azure.ai.openai.models.CompletionsFinishReason
import io.github.lmos.arc.agents.ArcException
import io.github.lmos.arc.agents.HallucinationDetectedException
import io.github.lmos.arc.agents.functions.LLMFunction
import io.github.lmos.arc.agents.functions.convertToJsonMap
import io.github.lmos.arc.core.failWith
import io.github.lmos.arc.core.getOrNull
import io.github.lmos.arc.core.result
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Finds function calls in ChatCompletions and calls the callback function if any are found.
 */
class FunctionCallHandler(private val functions: List<LLMFunction>) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val _calledFunctions = ConcurrentHashMap<String, LLMFunction>()
    val calledFunctions get(): Map<String, LLMFunction> = _calledFunctions

    fun calledSensitiveFunction() = _calledFunctions.any { it.value.isSensitive }

    suspend fun handle(chatCompletions: ChatCompletions) = result<List<ChatRequestMessage>, ArcException> {
        val choice = chatCompletions.choices[0]

        // The LLM is requesting the calling of the function we defined in the original request
        if (CompletionsFinishReason.TOOL_CALLS == choice.finishReason) {
            val assistantMessage = ChatRequestAssistantMessage("")
            assistantMessage.setToolCalls(choice.message.toolCalls)

            log.debug("Received ${choice.message.toolCalls.size} tool calls..")
            val toolMessages = buildList {
                choice.message.toolCalls.forEach {
                    val toolCall = it as ChatCompletionsFunctionToolCall
                    val functionName = toolCall.function.name
                    val functionArguments = toolCall.function.arguments

                    val functionCallResult = callFunction(functionName, functionArguments) failWith { it }
                    add(ChatRequestToolMessage(functionCallResult, toolCall.id))
                }
            }
            listOf(assistantMessage) + toolMessages
        } else {
            emptyList()
        }
    }

    private suspend fun callFunction(functionName: String, functionArguments: String) = result<String, ArcException> {
        val jsonMap = functionArguments.toJson() failWith { it }
        val function = functions.find { it.name == functionName }
            ?: failWith { ArcException("Cannot find function called $functionName!") }

        log.debug("Calling LLMFunction $function with $jsonMap...")
        _calledFunctions[functionName] = function
        function.execute(jsonMap) failWith { ArcException(cause = it.cause) }
    }

    private fun String.toJson() = result<Map<String, Any?>, HallucinationDetectedException> {
        convertToJsonMap().getOrNull() ?: failWith {
            HallucinationDetectedException("LLM has failed to produce valid JSON for function call! -> ${this@toJson}")
        }
    }
}
