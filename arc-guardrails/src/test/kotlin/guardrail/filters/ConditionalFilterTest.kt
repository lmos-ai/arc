// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.UserMessage
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConditionalFilterTest {

    @Test
    fun `should apply true filters when condition is met`() = runTest {
        // Arrange
        val condition = Condition.Equals("Trigger")
        val trueFilter = LengthFilter(5)
        val falseFilter = LengthFilter(50)
        val filter = ConditionalFilter(
            condition,
            listOf(trueFilter),
            listOf(falseFilter),
            mockk(),
        )
        val message = UserMessage(
            content = "Trigger",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "Trigg"
        assertEquals(expectedContent, result?.content)
    }

    @Test
    fun `should apply false filters when condition is not met`() = runTest {
        // Arrange
        val condition = Condition.Equals("Trigger")
        val trueFilter = LengthFilter(5)
        val falseFilter = LengthFilter(9)
        val filter = ConditionalFilter(
            condition,
            listOf(trueFilter),
            listOf(falseFilter),
            mockk(),
        )
        val message = UserMessage(
            content = "No match here.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "No match "
        assertEquals(expectedContent, result?.content)
    }
}
