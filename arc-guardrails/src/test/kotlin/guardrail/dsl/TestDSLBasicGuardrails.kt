// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.dsl

import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.InputFilterContext
import ai.ancf.lmos.arc.guardrail.extensions.guardrails
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestDSLBasicGuardrails {

    @Test
    fun `should apply profanity filter defined in DSL`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)
        val inputFilterContext = InputFilterContext(
            context,
            Conversation(
                transcript = mutableListOf(
                    UserMessage(
                        content = "This is a badword.",
                    ),
                ),
                user = User(id = "user"),
            ),
        )

        // Act
        inputFilterContext.guardrails {
            filter("profanity") {
                replace("badword", "***")
            }
        }

        // Assert
        val filteredMessage = inputFilterContext.inputMessage
        assertEquals("This is a ***.", filteredMessage.content)
    }

    @Test
    fun `should apply length filter defined in DSL`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)
        val message = UserMessage(
            content = "This message is too long.",
        )

        val inputFilterContext = InputFilterContext(
            context,
            Conversation(
                transcript = mutableListOf(message),
                user = User(id = "user"),
            ),
        )

        // Act
        inputFilterContext.guardrails {
            length(10)
        }

        // Assert
        val filteredMessage = inputFilterContext.inputMessage
        assertEquals("This messa", filteredMessage.content)
    }
}
