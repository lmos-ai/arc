package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import ai.ancf.lmos.arc.agents.dsl.get
import ai.ancf.lmos.arc.agents.memory.InMemoryMemory
import ai.ancf.lmos.arc.agents.memory.Memory
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CustomReplacementContextPreFilterTest {

    @Test
    fun `should replace sensitive information with placeholders`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)

        coEvery { context.get<Conversation>() } returns Conversation(
            user = User(id = "default"),
            conversationId = "test",
            currentTurnId = "test",
        )
        coEvery { context.get<Memory>() } returns InMemoryMemory()

        context.memory("knowledge", "Email: user@example.com")

        val patterns = listOf(
            Pair("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})", "EMAIL"),
        )

        val filter = CustomReplacementContextPreFilter(context, patterns)
        val message = UserMessage(
            content = "Email: user@example.com",
            turnId = "test",
        )

        // Act
        val result = filter.filter("knowledge", message)
        // Assert
        val expectedContent = "Email: user@example.com"
        val expectedContext = "Email: EMAIL-1"
        val memory = context.memory("knowledge") as String

        assertEquals(memory, expectedContext)
        assertEquals(expectedContent, result.content)
    }
}

class CustomReplacementPreFilterTest {

    @Test
    fun `should replace sensitive information with placeholders`() = runTest {
        // Arrange
        val patterns = listOf(
            Pair("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})", "EMAIL"),
            Pair("\\d{3}-\\d{2}-\\d{4}", "PHONE"),
            Pair("https?://\\S+", "URL"),
        )
        val context = mockk<DSLContext>(relaxed = true)
        val filter = CustomReplacementPreFilter(context, patterns)
        val message = UserMessage(
            content = "Email: user@example.com, Phone: 123-45-6789, URL: https://example.com",
            turnId = "123",
        )
        coEvery { context.memory(any(), any()) } just Runs

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "Email: EMAIL-1, Phone: PHONE-1, URL: URL-1"
        assertEquals(expectedContent, result.content)
        coEvery { context.memory("placeholders_123", any<Map<String, String>>()) }
    }
}
