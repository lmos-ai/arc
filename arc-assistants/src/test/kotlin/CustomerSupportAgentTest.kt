// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.assistants.support

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CustomerSupportAgentTest : TestBase() {

    @Test
    fun `test assistant creation`(): Unit = runBlocking {
        val assistant = assistantContext {
            customerSupportAgent {
                name = "Customer Support"
                companyName = "Deutsche Telekom"
                knowledge {
                    """
                    - I can help you with your phone
                    - I can help you with your internet
                    """
                }
                examples {
                    """
                    - I have a problem with my phone
                    - I need help with my internet
                    - I want to cancel my contract
                    """
                }
            }
        }

        assertThat(assistant.name).isEqualTo("Customer Support")
    }
}
