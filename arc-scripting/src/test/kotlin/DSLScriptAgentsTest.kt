// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting

import ai.ancf.lmos.arc.core.getOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DSLScriptAgentsTest : TestBase() {

    @Test
    fun `test loading agents`() {
        val agentBuilder = DSLScriptAgents.init(chatCompleterProvider)
        agentBuilder.define(
            """
            agent {
                name = "agent"
                description = "agent description"
                prompt { "does stuff" }
            }
        """,
        ).getOrThrow()
        val result = agentBuilder.getAgents()
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("agent")
    }

    @Test
    fun `test loading functions`() {
        val agentBuilder = DSLScriptAgents.init(chatCompleterProvider)
        agentBuilder.defineFunctions(
            """
            function(
                name = "get_weather",
                description = "the weather service",
                params = types(string("location", "the location")),
            ) {
                "result"
            }
        """,
        ).getOrThrow()
        val result = agentBuilder.provideAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("get_weather")
    }
}