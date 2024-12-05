// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.api

import kotlinx.serialization.Serializable

/**
 * AgentRequest
 */
@Serializable
data class AgentRequest(
    val messages: List<Message>,
    val conversationContext: ConversationContext,
    val systemContext: List<SystemContextEntry>? = null,
    val userContext: UserContext? = null,
)

@Serializable
data class UserContext(
    val userId: String? = null,
    val userToken: String? = null,
    val profile: List<ProfileEntry>? = null,
)

@Serializable
data class ConversationContext(
    val conversationId: String,
    val turnId: String? = null,
    val anonymizationEntities: List<AnonymizationEntity>? = null,
)

@Serializable
data class ProfileEntry(
    val key: String,
    val value: String,
)

@Serializable
data class SystemContextEntry(
    val key: String,
    val value: String,
)

/**
 * Short-hand function to create an agent request with a single user message.
 */
fun agentRequest(content: String, conversationId: String, vararg binaryData: BinaryData, turnId: String? = null) =
    AgentRequest(
        messages = listOf(
            Message(
                "user", content,
                turnId = turnId,
                binaryData = binaryData.toList()
            )
        ),
        conversationContext = ConversationContext(conversationId, turnId)
    )