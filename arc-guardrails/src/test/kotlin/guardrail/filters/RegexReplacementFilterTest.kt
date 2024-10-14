package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.UserMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegexReplacementFilterTest {

    @Test
    fun `should replace content matching regex pattern`() = runTest {
        // Arrange
        val pattern = "\\d{4}-\\d{2}-\\d{2}"
        val replacement = "[DATE]"
        val filter = RegexReplacementFilter(pattern, replacement)
        val message = UserMessage(
            content = "Today's date is 2024-09-16.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "Today's date is [DATE]."
        assertEquals(expectedContent, result.content)
    }

    @Test
    fun `should not modify message if pattern not found`() = runTest {
        // Arrange
        val pattern = "\\d{4}-\\d{2}-\\d{2}"
        val replacement = "[DATE]"
        val filter = RegexReplacementFilter(pattern, replacement)
        val message = UserMessage(
            content = "No date here.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        assertEquals(message.content, result.content)
    }
}
