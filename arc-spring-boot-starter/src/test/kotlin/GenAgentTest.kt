// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.spring

import ai.ancf.lmos.arc.agents.AgentProvider
import ai.ancf.lmos.arc.agents.getAgentByName
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "arc.scripts.enabled=false",
    ]
)
class GenAgentTest {

    @Autowired
    lateinit var agentProvider: AgentProvider

    @Test
    fun `test gen agent is loaded`(): Unit = runBlocking {
        assertThat(agentProvider.getAgentByName("weather-gen")).isNotNull
    }
}
