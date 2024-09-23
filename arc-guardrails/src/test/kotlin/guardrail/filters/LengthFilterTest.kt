package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.UserMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LengthFilterTest {

    @Test
    fun `should truncate message exceeding max length`() = runTest {
        // Arrange
        val maxLength = 10
        val filter = LengthFilter(maxLength)
        val message = UserMessage(
            content = "This message is too long.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "This messa"
        assertEquals(expectedContent, result.content)
    }

    @Test
    fun `should not modify message within max length`() = runTest {
        // Arrange
        val maxLength = 50
        val filter = LengthFilter(maxLength)
        val message = UserMessage(
            content = "Short message.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        assertEquals(message.content, result.content)
    }
}
