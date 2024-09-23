package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TryCatchFilterTest {

    @Test
    fun `should apply try filters without error`() = runTest {
        // Arrange
        val tryFilter = LengthFilter(10)
        val errorHandler = ErrorHandler.Log("An error occurred.")
        val filter = TryCatchFilter(
            tryFilters = listOf(tryFilter),
            errorHandlers = listOf(errorHandler),
            context = mockk(relaxed = true),
        )
        val message = UserMessage(
            content = "This is a test message.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "This is a "
        assertEquals(expectedContent, result?.content)
    }

    @Test
    fun `should handle exception and execute error handlers`() = runTest {
        // Arrange
        val faultyFilter = mockk<AgentFilter>()
        val error = RuntimeException("Test exception")
        coEvery { faultyFilter.filter(any()) } throws error
        val errorHandler = ErrorHandler.Log("An error occurred.")
        val context = mockk<DSLContext>(relaxed = true)
        val filter = TryCatchFilter(
            tryFilters = listOf(faultyFilter),
            errorHandlers = listOf(errorHandler),
            context = context,
        )
        val message = UserMessage(
            content = "Test message.",
        )

        // Act
        val result = filter.filter(message)

        // Assert
        assertEquals(message, result)
    }
}
