package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.UserMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProfanityFilterTest {

    @Test
    fun `should replace profane words with replacements`() = runTest {
        // Arrange
        val replacements = mapOf("badword" to "***", "superbad" to "###")
        val filter = ProfanityFilter(replacements)
        val message = UserMessage(
            content = "This is a badword and superbad.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "This is a *** and ###."
        assertEquals(expectedContent, result.content)
    }

    @Test
    fun `should handle case-insensitive replacements`() = runTest {
        // Arrange
        val replacements = mapOf("badword" to "***")
        val filter = ProfanityFilter(replacements)
        val message = UserMessage(
            content = "This is a BADWORD.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "This is a ***."
        assertEquals(expectedContent, result.content)
    }

    @Test
    fun `should not modify message if no profane words are present`() = runTest {
        // Arrange
        val replacements = mapOf("badword" to "***")
        val filter = ProfanityFilter(replacements)
        val message = UserMessage(
            content = "This is a clean message.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        assertEquals(message.content, result.content)
    }
}
