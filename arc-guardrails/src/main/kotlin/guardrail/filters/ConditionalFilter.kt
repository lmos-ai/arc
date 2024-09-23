package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext

class ConditionalFilter(
    private val condition: Condition,
    private val trueFilters: List<AgentFilter>,
    private val falseFilters: List<AgentFilter>,
    private val context: DSLContext,
) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage? {
        val conditionMet = evaluateCondition(condition, message, context)
        val filtersToApply = if (conditionMet) trueFilters else falseFilters
        var currentMessage: ConversationMessage? = message
        for (filter in filtersToApply) {
            currentMessage = currentMessage?.let { filter.filter(it) }
            if (currentMessage == null) break
        }
        return currentMessage
    }
}
