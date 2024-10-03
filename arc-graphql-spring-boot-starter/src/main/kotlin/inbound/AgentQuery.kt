// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.graphql.inbound

import ai.ancf.lmos.arc.agents.AgentProvider
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import kotlinx.serialization.Serializable

/**
 * Returns the list of available Agents.
 */
class AgentQuery(private val agentProvider: AgentProvider) : Query {

    @GraphQLDescription("Returns the list of available Agents.")
    fun agent(): Agents {
        return Agents(agentProvider.getAgents().map { it.name })
    }
}

@Serializable
data class Agents(val names: List<String>)
