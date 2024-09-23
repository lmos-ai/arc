package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.AgentFailedException
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BlacklistFilterTest {

    @Test
    fun `should throw exception if blacklisted term is present`() = runTest {
        // Arrange
        val blacklist = listOf("forbiddenWord", "restrictedTerm")
        val filter = BlacklistFilter(blacklist)
        val message = UserMessage(
            content = "This contains a forbiddenWord.",
        )

        // Act & Assert
        val exception = assertThrows<AgentFailedException> {
            filter.filter(message)
        }
        assertEquals("Message contains blacklisted term: forbiddenWord", exception.message)
    }

    @Test
    fun `should not modify message if no blacklisted terms are present`() = runTest {
        // Arrange
        val blacklist = listOf("forbiddenWord", "restrictedTerm")
        val filter = BlacklistFilter(blacklist)
        val message = UserMessage(
            content = "This is a clean message.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        assertEquals(message, result)
    }
}
