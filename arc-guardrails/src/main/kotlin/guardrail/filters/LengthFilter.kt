// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter

class LengthFilter(private val maxLength: Int) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        val content = if (message.content.length > maxLength) {
            message.content.substring(0, maxLength)
        } else {
            message.content
        }
        return message.update(content)
    }
}
