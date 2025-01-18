// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.client.ollama

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val format: String?,
    val stream: Boolean = false,
    val tools: List<Tool>? = null,
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
    val images: List<String>? = null,
    @SerialName("tool_calls")
    val toolCalls: List<ToolCall>? = null,
)

@Serializable
data class ChatResponse(
    @SerialName("model")
    val model: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    val message: ChatMessage,
    @SerialName("done_reason")
    val doneReason: String? = null,
    val done: Boolean? = null,
    @SerialName("total_duration")
    val totalDuration: Long? = null,
    @SerialName("load_duration")
    val loadDuration: Long? = null,
    @SerialName("prompt_eval_count")
    val promptEvalCount: Int,
    @SerialName("prompt_eval_duration")
    val promptEvalDuration: Long,
    @SerialName("eval_count")
    val evalCount: Int,
    @SerialName("eval_duration")
    val evalDuration: Long? = null,
)

@Serializable
data class ToolCall(
    val function: FunctionCall,
)

@Serializable
data class FunctionCall(
    val name: String,
    val arguments: Map<String, String>,
)

@Serializable
data class Tool(
    val type: String,
    val function: Function,
)

@Serializable
data class Function(
    val name: String,
    val description: String,
    val parameters: Parameters?,
)

@Serializable
data class Parameters(
    val type: String,
    val properties: Map<String, Property>,
    val required: List<String>,
)

@Serializable
data class Property(
    val type: String,
    val description: String,
    val enum: List<String>? = null,
)

@Serializable
data class TextEmbeddingRequest(
    val model: String,
    val prompt: String,
)
