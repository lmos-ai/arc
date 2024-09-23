// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.extensions

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.InputFilterContext
import ai.ancf.lmos.arc.agents.dsl.OutputFilterContext
import ai.ancf.lmos.arc.guardrail.dsl.GuardrailBuilder

suspend fun InputFilterContext.applyGuardrails(block: GuardrailBuilder.() -> Unit) {
    val guardrailFilters = GuardrailBuilder(this.scriptingContext).apply(block).build()
    this.mapLatest { message ->
        applyFiltersSequentially(message, guardrailFilters)
    }
}

suspend fun OutputFilterContext.applyGuardrails(block: GuardrailBuilder.() -> Unit) {
    val guardrailFilters = GuardrailBuilder(this.scriptingContext).apply(block).build()
    this.mapLatest { message ->
        applyFiltersSequentially(message, guardrailFilters)
    }
}

private suspend fun applyFiltersSequentially(
    message: ConversationMessage,
    filters: List<AgentFilter>,
): ConversationMessage? {
    var currentMessage: ConversationMessage? = message
    for (filter in filters) {
        try {
            currentMessage = currentMessage?.let { filter.filter(it) }
            if (currentMessage == null) {
                break
            }
        } catch (e: Exception) {
            throw e
        }
    }
    return currentMessage
}

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
