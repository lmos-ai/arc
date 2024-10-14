// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.extensions

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.InputFilterContext
import ai.ancf.lmos.arc.agents.dsl.OutputFilterContext
import ai.ancf.lmos.arc.guardrail.dsl.GuardrailBuilder

/**
 * Add a group of filters to the input filter context.
 */
suspend fun InputFilterContext.guardrails(block: GuardrailBuilder.() -> Unit) {
    val guardrailFilters = GuardrailBuilder(this.scriptingContext).apply(block).build()
    this.mapLatest { message ->
        applyFiltersSequentially(message, guardrailFilters)
    }
}

/**
 * Add a group of filters to the output filter context.
 */
suspend fun OutputFilterContext.guardrails(block: GuardrailBuilder.() -> Unit) {
    val guardrailFilters = GuardrailBuilder(this.scriptingContext).apply(block).build()
    this.mapLatest { message ->
        applyFiltersSequentially(message, guardrailFilters)
    }
}

/**
 * Function to apply a list of filters to a message sequentially.
 */
private suspend fun applyFiltersSequentially(
    message: ConversationMessage,
    filters: List<AgentFilter>,
): ConversationMessage? {
    var currentMessage: ConversationMessage? = message
    for (filter in filters) {
        currentMessage = currentMessage?.let { filter.filter(it) }
        if (currentMessage == null) {
            break
        }
    }
    return currentMessage
}

/**
 * A filter group that applies a list of filters sequentially.
 */
class FilterGroup(private val filters: List<AgentFilter>) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage? {
        var currentMessage: ConversationMessage? = message
        for (filter in filters) {
            currentMessage = currentMessage?.let { filter.filter(it) }
            if (currentMessage == null) break
        }
        return currentMessage
    }
}
