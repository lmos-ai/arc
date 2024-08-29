// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package ai.ancf.lmos.arc.spring.ai

import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.conversation.SystemMessage
import ai.ancf.lmos.arc.agents.conversation.UserMessage

/**
 * Converts a list of ConversationMessages to a list of Spring AI messages.
 */
fun List<ConversationMessage>.toSpringAI() = map { msg ->
    when (msg) {
        is UserMessage -> org.springframework.ai.chat.messages.UserMessage(msg.content)
        is AssistantMessage -> org.springframework.ai.chat.messages.AssistantMessage(msg.content)
        is SystemMessage -> org.springframework.ai.chat.messages.SystemMessage(msg.content)
    }
}
