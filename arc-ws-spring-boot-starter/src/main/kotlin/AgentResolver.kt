// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.ws

import ai.ancf.lmos.arc.agents.Agent
import ai.ancf.lmos.arc.api.AgentRequest

/**
 * Resolves the agent for the given request.
 * Returns null if no agent is found.
 */
interface AgentResolver {

    fun resolveAgent(agentName: String? = null, request: AgentRequest): Agent<*, *>?
}
