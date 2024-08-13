package io.github.lmos.arc.graphql.inbound

import com.expediagroup.graphql.server.operations.Query
import io.github.lmos.arc.agents.AgentProvider
import kotlinx.serialization.Serializable

/**
 * Returns the list of available Agents.
 */
class AgentQuery(private val agentProvider: AgentProvider) : Query {

    fun agent(): Agents {
        return Agents(agentProvider.getAgents().map { it.name })
    }
}

@Serializable
data class Agents(val names: List<String>)
