// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.breakWith

/**
 * A filter that checks if a message contains any blacklisted terms.
 */
class BlacklistFilter(private val context: DSLContext, private val blacklist: List<String>) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        for (term in blacklist) {
            if (message.content.contains(term, ignoreCase = true)) {
                context.breakWith(
                    message = "Sorry, I am currently unable to process your request.",
                    reason = "Message contains blacklisted term: $term",
                )
            }
        }
        return message
    }
}
