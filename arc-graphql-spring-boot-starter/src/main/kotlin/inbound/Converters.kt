// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.graphql.inbound

import org.eclipse.lmos.arc.agents.conversation.AssistantMessage
import org.eclipse.lmos.arc.agents.conversation.UserMessage
import org.eclipse.lmos.arc.api.AnonymizationEntity
import org.eclipse.lmos.arc.api.Message

fun List<Message>.convert(stream: DataStream? = null): List<ConversationMessage> = map {
    when (it.role) {
        "user" -> UserMessage(it.content, binaryData = it.binaryData?.convertBinary(stream) ?: emptyList())
        "assistant" -> AssistantMessage(it.content)
        else -> throw IllegalArgumentException("Unknown role: ${it.role}")
    }
}

fun AssistantMessage?.toMessage() = Message("assistant", this?.content ?: "", turnId = this?.turnId)

fun List<AnonymizationEntity>?.convertConversationEntities() = this?.map {
    org.eclipse.lmos.arc.agents.conversation.AnonymizationEntity(
        type = it.type,
        value = it.value,
        replacement = it.replacement,
    )
} ?: emptyList()

fun List<org.eclipse.lmos.arc.agents.conversation.AnonymizationEntity>?.convertAPIEntities() = this?.map {
    AnonymizationEntity(
        type = it.type,
        value = it.value,
        replacement = it.replacement,
    )
} ?: emptyList()

/**
 * Converts a list of [BinaryData] to a list of core [BinaryData].
 */
fun List<BinaryData>.convertBinary(stream: DataStream?) =
    map {
        CoreBinaryData(
            it.mimeType,
            stream = when (it.source) {
                STREAM_SOURCE -> stream ?: error("Stream source provided but streaming not enabled!")
                else -> it.dataAsBase64?.asDataStream() ?: error("No data or source provided!")
            }
        )
    }