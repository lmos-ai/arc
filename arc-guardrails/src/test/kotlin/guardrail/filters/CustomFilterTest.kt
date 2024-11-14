// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.UserMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CustomFilterTest {

    @Test
    fun `should replace patterns using regex`() = runTest {
        // Arrange
        val replacements = mapOf("\\d+" to "[NUMBER]")
        val filter = CustomFilter(replacements)
        val message = UserMessage(
            content = "My phone number is 1234567890.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "My phone number is [NUMBER]."
        assertEquals(expectedContent, result.content)
    }

    @Test
    fun `should handle multiple patterns`() = runTest {
        // Arrange
        val replacements = mapOf(
            "\\d+" to "[NUMBER]",
            "\\buser@example\\.com\\b" to "[EMAIL]",
        )
        val filter = CustomFilter(replacements)
        val message = UserMessage(
            content = "Contact me at user@example.com or call 1234567890.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "Contact me at [EMAIL] or call [NUMBER]."
        assertEquals(expectedContent, result.content)
    }
}
