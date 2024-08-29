// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting.agents

import ai.ancf.lmos.arc.agents.dsl.AgentDefinitionContext

/**
 * Interface for loading compiled agents.
 */
interface CompiledAgentLoader {

    fun load(agentDefinitionContext: AgentDefinitionContext)
}
