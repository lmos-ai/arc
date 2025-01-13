// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.client.ollama

import org.eclipse.lmos.arc.agents.ArcException
import org.eclipse.lmos.arc.agents.events.EventPublisher
import org.eclipse.lmos.arc.agents.functions.LLMFunction
import org.eclipse.lmos.arc.agents.functions.LLMFunctionCalledEvent
import org.eclipse.lmos.arc.agents.functions.LLMFunctionStartedEvent
import org.eclipse.lmos.arc.core.Result
import org.eclipse.lmos.arc.core.failWith
import org.eclipse.lmos.arc.core.result
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTime

/**
 * Finds function calls in ChatResponses and calls the callback function if any are found.
 */
class FunctionCallHandler(
    private val functions: List<LLMFunction>,
    private val eventHandler: EventPublisher?,
    private val functionCallLimit: Int = 60,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val functionCallCount = AtomicInteger(0)

    private val _calledFunctions = ConcurrentHashMap<String, LLMFunction>()
    val calledFunctions get(): Map<String, LLMFunction> = _calledFunctions

    fun calledSensitiveFunction() = _calledFunctions.any { it.value.isSensitive }

    suspend fun handle(chatResponse: ChatResponse) = result<List<ChatMessage>, ArcException> {
        val isToolCall = chatResponse.message.toolCalls?.isNotEmpty() ?: false

        if (functionCallCount.incrementAndGet() > functionCallLimit) {
            failWith {
                ArcException("Function call limit exceeded!")
            }
        }

        if (isToolCall) {
            val message = chatResponse.message

            val assistantMessage = ChatMessage(
                role = "assistant",
                content = message.content,
                toolCalls = message.toolCalls,
            )

            log.debug("Received ${message.toolCalls?.size ?: 0} tool calls..")
            val toolMessages = buildList {
                message.toolCalls?.forEach {
                    val toolCall = it
                    val functionName = toolCall.function.name
                    val functionArguments = toolCall.function.arguments

                    val functionCallResult: Result<String, ArcException>
                    val duration = measureTime {
                        eventHandler?.publish(LLMFunctionStartedEvent(functionName, functionArguments))
                        functionCallResult = callFunction(functionName, functionArguments)
                    }
                    eventHandler?.publish(
                        LLMFunctionCalledEvent(
                            functionName,
                            functionArguments,
                            functionCallResult,
                            duration = duration,
                        ),
                    )

                    add(
                        ChatMessage(
                            role = "tool",
                            content = functionCallResult failWith { it },
                        ),
                    )
                }
            }
            listOf(assistantMessage) + toolMessages
        } else {
            emptyList()
        }
    }

    private suspend fun callFunction(functionName: String, functionArguments: Map<String, Any?>) =
        result<String, ArcException> {
            val function = functions.find { it.name == functionName }
                ?: failWith { ArcException("Cannot find function called $functionName!") }

            log.info("Calling LLMFunction $function")
            _calledFunctions[functionName] = function
            function.execute(functionArguments) failWith { ArcException(cause = it.cause) }
        }
}
