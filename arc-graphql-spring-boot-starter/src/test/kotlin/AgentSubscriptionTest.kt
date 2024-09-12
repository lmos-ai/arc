// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.graphql

import ai.ancf.lmos.arc.graphql.inbound.AgentSubscription
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AgentSubscriptionTest {

    @Autowired
    lateinit var agentSubscription: AgentSubscription

    @Test
    fun `test agentSubscription is defined`(): Unit = runBlocking {
        assertThat(agentSubscription).isNotNull
    }
}
