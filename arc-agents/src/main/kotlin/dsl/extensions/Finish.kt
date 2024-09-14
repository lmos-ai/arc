// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.dsl.extensions

import ai.ancf.lmos.arc.agents.WithConversationResult
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.ConversationClassification
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.get

/**
 * Cancels the current execution of the Agent and returns the given message.
 */
suspend fun DSLContext.breakWithMessage(
    message: String,
    classification: ConversationClassification? = null,
    reason: String? = null,
): Any? {
    val conversationResult = get<Conversation>().copy(classification = classification) + AssistantMessage(message)
    throw InterruptProcessingException(conversationResult, reason)
}

class InterruptProcessingException(override val conversation: Conversation, reason: String?) :
    Exception("Agent processing interrupted. Reason:[${reason ?: "none"}]"), WithConversationResult
