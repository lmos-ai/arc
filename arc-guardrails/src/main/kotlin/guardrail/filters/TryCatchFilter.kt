// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.breakWith
import ai.ancf.lmos.arc.agents.dsl.extensions.error

class TryCatchFilter(
    private val tryFilters: List<AgentFilter>,
    private val errorHandlers: List<ErrorHandler>,
    private val context: DSLContext,
) : AgentFilter {

    override suspend fun filter(message: ConversationMessage): ConversationMessage? {
        try {
            var currentMessage: ConversationMessage? = message
            for (filter in tryFilters) {
                currentMessage = currentMessage?.let { filter.filter(it) }
                if (currentMessage == null) break
            }
            return currentMessage
        } catch (e: Exception) {
            handleErrors(e, context)
            return message // Or return null to remove the message
        }
    }

    private suspend fun handleErrors(exception: Exception, context: DSLContext) {
        for (handler in errorHandlers) {
            when (handler) {
                is ErrorHandler.Log -> context.error(handler.message, exception)
                is ErrorHandler.Break -> context.breakWith(
                    message = "Sorry, I am currently unable to process your request.",
                    reason = handler.message,
                )
                // Add more error handling logic here
            }
        }
    }
}
