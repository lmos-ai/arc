// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.breakWith
import ai.ancf.lmos.arc.agents.dsl.extensions.memory

class ContextValidationFilter(private val context: DSLContext, private val contextVariableName: String) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        if (context.memory(contextVariableName) == null) {
            context.breakWith(
                message = "Sorry, I am currently unable to process your request.",
                reason = "Context variable $contextVariableName does not exist",
            )
        }
        return message
    }
}
