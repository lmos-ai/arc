package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CustomReplacementPostFilterTest {

    @Test
    fun `should restore original values from placeholders`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)
        val placeholders = mapOf(
            "[EMAIL-1]" to "user@example.com",
            "[PHONE-1]" to "123-45-6789",
            "[URL-1]" to "https://example.com",
        )
        coEvery { context.memory("placeholders_123") } returns placeholders
        val filter = CustomReplacementPostFilter(context)
        val message = AssistantMessage(
            content = "Email: [EMAIL-1], Phone: [PHONE-1], URL: [URL-1]",
            turnId = "123",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "Email: user@example.com, Phone: 123-45-6789, URL: https://example.com"
        assertEquals(expectedContent, result.content)
    }
}
