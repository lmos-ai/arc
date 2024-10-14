package ai.ancf.lmos.arc.guardrail.dsl

import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.InputFilterContext
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import ai.ancf.lmos.arc.agents.dsl.get
import ai.ancf.lmos.arc.agents.memory.InMemoryMemory
import ai.ancf.lmos.arc.agents.memory.Memory
import ai.ancf.lmos.arc.guardrail.extensions.guardrails
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestDSLContextPreProcessing {

    @Test
    fun `should preprocess context using DSL`() = runTest {
        // Arrange
        val context = spyk<DSLContext>()
        coEvery { context.get<Conversation>() } returns Conversation(
            user = User("user"),
        )

        coEvery { context.get<Memory>() } returns InMemoryMemory()

        context.memory("documents", "Email: test@example.com, Phone: 987-65-4321")
        val message = UserMessage(
            content = "Email: user@example.com, Phone: 123-45-6789",
            turnId = "123",
        )
        val inputFilterContext = InputFilterContext(
            context,
            Conversation(
                transcript = listOf(message),
                user = User("user"),
            ),
        )

        // Act
        inputFilterContext.guardrails {
            contextpreprocess("documents") {
                patterns = listOf(
                    Pair("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})", "EMAIL"),
                    Pair("\\d{3}-\\d{2}-\\d{4}", "PHONE"),
                )
            }
        }

        // Assert
        // TODO - Fix this test
        val filteredMessage = inputFilterContext.inputMessage
        assertEquals(message.content, filteredMessage.content)
        assertEquals(context.memory("documents"), "Email: EMAIL-1, Phone: PHONE-1")
    }

    @Test
    fun `should preprocess context using DSL with count in type`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)
        val message = UserMessage(
            content = "Email: user@example.com, Phone: 123-45-6789",
            turnId = "123",
        )
        val inputFilterContext = InputFilterContext(
            context,
            Conversation(
                transcript = listOf(message),
                user = User("user"),
            ),
        )

        coEvery { context.memory(any(), any()) } just Runs
        coEvery { context.memory(any()) } returns mapOf(
            "https://link-1.com" to "user@example.com",
            "PHONE-1" to "123-45-6789",
        )

        // Act
        inputFilterContext.guardrails {
            preprocess {
                patterns = listOf(
                    Pair("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})", "https://link-{count}.com"),
                    Pair("\\d{3}-\\d{2}-\\d{4}", "PHONE-{count}"),
                )
            }
            postprocess {}
        }

        // Assert
        val filteredMessage = inputFilterContext.inputMessage
        assertEquals(message.content, filteredMessage.content)
    }
}
