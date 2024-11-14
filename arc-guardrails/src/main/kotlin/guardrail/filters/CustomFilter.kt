// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter

/**
 * A filter that replaces text in a message based on a map of replacements.
 */
class CustomFilter(private val replacements: Map<String, String>) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        var content = message.content
        for ((pattern, replacement) in replacements) {
            content = content.replace(Regex(pattern), replacement)
        }
        return message.update(content)
    }
}
