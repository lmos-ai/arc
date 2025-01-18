// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.graphql.inbound

import org.eclipse.lmos.arc.agents.conversation.*
import org.eclipse.lmos.arc.api.AnonymizationEntity
import org.eclipse.lmos.arc.api.BinaryData
import org.eclipse.lmos.arc.api.Message
import org.eclipse.lmos.arc.api.STREAM_SOURCE

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
        org.eclipse.lmos.arc.agents.conversation.BinaryData(
            it.mimeType,
            stream = when (it.source) {
                STREAM_SOURCE -> stream ?: error("Stream source provided but streaming not enabled!")
                else -> it.dataAsBase64?.asDataStream() ?: error("No data or source provided!")
            },
        )
    }
