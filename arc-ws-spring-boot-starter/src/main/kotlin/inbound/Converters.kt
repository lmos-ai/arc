// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.ws.inbound

import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.api.AnonymizationEntity
import ai.ancf.lmos.arc.api.BinaryData
import ai.ancf.lmos.arc.api.Message
import ai.ancf.lmos.arc.agents.conversation.AnonymizationEntity as CoreAnonymizationEntity
import ai.ancf.lmos.arc.agents.conversation.BinaryData as CoreBinaryData

fun List<Message>.convert(dataProvider: WritableDataStream): List<ConversationMessage> = map {
    when (it.role) {
        "user" -> UserMessage(it.content, binaryData = it.binaryData?.convertBinary(dataProvider) ?: emptyList())
        "assistant" -> AssistantMessage(it.content)
        else -> throw IllegalArgumentException("Unknown role: ${it.role}")
    }
}

fun AssistantMessage?.toMessage() = Message("assistant", this?.content ?: "", turnId = this?.turnId)

fun List<AnonymizationEntity>?.convertConversationEntities() = this?.map {
    ai.ancf.lmos.arc.agents.conversation.AnonymizationEntity(
        type = it.type,
        value = it.value,
        replacement = it.replacement,
    )
} ?: emptyList()

fun List<CoreAnonymizationEntity>?.convertAPIEntities() = this?.map {
    AnonymizationEntity(
        type = it.type,
        value = it.value,
        replacement = it.replacement,
    )
} ?: emptyList()

fun List<BinaryData>.convertBinary(dataProvider: WritableDataStream) =
    map { CoreBinaryData(it.mimeType, null, dataProvider) }
