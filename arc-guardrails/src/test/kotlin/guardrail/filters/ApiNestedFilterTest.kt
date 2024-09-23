package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import ai.ancf.lmos.arc.guardrail.dsl.ApiBodyBuilder
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApiNestedFilterTest {

    @Test
    fun `should make API call and apply nested filters`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)
        val bodyBuilder = ApiBodyBuilder().apply {
            query("param", "value")
        }
        val nestedFilter = LengthFilter(5)
        val filter = ApiNestedFilter("https://api.example.com", bodyBuilder, listOf(nestedFilter), context)
        val message = UserMessage(
            content = "Test message.",
            turnId = "123",
        )

        coEvery { context.httpGet(any()) } returns "API response"
        coEvery { context.memory(any(), any()) } just Runs

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "Test "
        assertEquals(expectedContent, result?.content)
        coVerify { context.memory("api_response_123", "API response") }
    }
}
