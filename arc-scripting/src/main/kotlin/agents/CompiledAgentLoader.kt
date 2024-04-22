// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.scripting.agents

import io.github.lmos.arc.agents.dsl.AgentDefinitionContext

/**
 * Interface for loading compiled agents.
 */
interface CompiledAgentLoader {

    fun load(agentDefinitionContext: AgentDefinitionContext)
}
