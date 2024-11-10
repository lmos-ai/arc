// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.assistants.support.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.breakWith
import ai.ancf.lmos.arc.assistants.support.Unresolved

context(DSLContext)
class UnresolvedDetector(
    private val fallbackReply: DSLContext.() -> String,
) : AgentFilter {

    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        if (message.content.contains("NO_ANSWER")) {
            breakWith(fallbackReply.invoke(this@DSLContext), classification = Unresolved)
        }
        if (message.content.contains(Unresolved.toString())) {
            breakWith(fallbackReply.invoke(this@DSLContext), classification = Unresolved)
        }
        return message
    }
}
