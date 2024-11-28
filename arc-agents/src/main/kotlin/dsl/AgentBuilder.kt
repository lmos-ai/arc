package ai.ancf.lmos.arc.agents.dsl

import ai.ancf.lmos.arc.agents.Agent

/**
 * Handy function to builds agents using the given [agentFactory] and [builder].
 * @return the list of agents created.
 */
fun buildAgents(agentFactory: AgentFactory<*>, builder: AgentDefinitionContext.() -> Unit): List<Agent<*, *>> {
    val context = BasicAgentDefinitionContext(agentFactory)
    with(context) {
        builder()
    }
    return context.agents.toList()
}