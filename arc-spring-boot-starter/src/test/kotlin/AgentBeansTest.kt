// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.spring

import io.github.lmos.arc.agents.AgentProvider
import io.github.lmos.arc.agents.functions.LLMFunctionProvider
import io.github.lmos.arc.agents.getAgentByName
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AgentBeansTest {

    @Autowired
    lateinit var agentProvider: AgentProvider

    @Autowired
    lateinit var functionProvider: LLMFunctionProvider

    @Test
    fun `test agent defined as bean`(): Unit = runBlocking {
        assertThat(agentProvider.getAgentByName("agentBean")).isNotNull
    }

    @Test
    fun `test function defined as bean`(): Unit = runBlocking {
        assertThat(functionProvider.provideByGroup("weather-beans")).isNotEmpty
    }
}
