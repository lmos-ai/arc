// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("ktlint")

package ai.ancf.lmos.arc.agents.gen

import ai.ancf.lmos.arc.agents.dsl.AgentDefinitionContext

class Agents {

    context(AgentDefinitionContext)
    fun build() {
        agent {
            name = "weather-gen"
        }
    }
}
