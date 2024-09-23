package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.UserMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class WhitelistValidationFilterTest {

    @Test
    fun `should allow message if all terms are in whitelist`() = runTest {
        // Arrange
        val whitelist = listOf("allowed", "terms", "message")
        val filter = WhitelistValidationFilter(whitelist, "Disallowed term detected.")
        val message = UserMessage(
            content = "allowed terms message",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        assertEquals(message, result)
    }

    @Test
    fun `should throw exception if message contains disallowed terms`() = runTest {
        // Arrange
        val whitelist = listOf("allowed", "terms")
        val filter = WhitelistValidationFilter(whitelist, "Disallowed term detected.")
        val message = UserMessage(
            content = "allowed terms forbidden",
        )

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            filter.filter(message)
        }
        assertEquals("Disallowed term detected. Found: forbidden", exception.message)
    }
}
