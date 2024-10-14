package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.InterruptProcessingException
import ai.ancf.lmos.arc.agents.dsl.get
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BlacklistFilterTest {

    @Test
    fun `should throw exception if blacklisted term is present`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)

        val blacklist = listOf("forbiddenWord", "restrictedTerm")
        val filter = BlacklistFilter(context, blacklist)
        val message = UserMessage(
            content = "This contains a forbiddenWord.",
        )
        coEvery { context.get<Conversation>() } returns Conversation(
            User("user"),
            transcript = listOf(message),
        )

        // Act & Assert
        assertThrows<InterruptProcessingException> {
            filter.filter(message)
        }
    }

    @Test
    fun `should not modify message if no blacklisted terms are present`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)

        val message = UserMessage(
            content = "This is a clean message.",
        )
        coEvery { context.get<Conversation>() } returns Conversation(
            User("user"),
            transcript = listOf(message),
        )
        val blacklist = listOf("forbiddenWord", "restrictedTerm")
        val filter = BlacklistFilter(context, blacklist)

        // Act
        val result = filter.filter(message)

        // Assert
        assertEquals(message, result)
    }
}
