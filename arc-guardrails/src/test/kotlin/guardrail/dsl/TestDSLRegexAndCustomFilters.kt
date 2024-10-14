package ai.ancf.lmos.arc.guardrail.dsl

import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.OutputFilterContext
import ai.ancf.lmos.arc.guardrail.extensions.guardrails
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestDSLRegexAndCustomFilters {

    @Test
    fun `should apply regex replacement defined in DSL`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)
        val outputFilterContext = OutputFilterContext(
            context,
            Conversation(
                transcript = mutableListOf(
                    AssistantMessage(
                        content = "The date is 2024-09-16.",
                    ),
                ),
                user = User(id = "user-id"),
            ),
            Conversation(
                transcript = mutableListOf(
                    AssistantMessage(
                        content = "The date is 2024-09-16.",
                    ),
                ),
                user = User(id = "user-id"),
            ),
            systemPrompt = "system-prompt",
        )

        // Act
        outputFilterContext.guardrails {
            regexReplacement("\\d{4}-\\d{2}-\\d{2}") {
                replace("[DATE]")
            }
        }

        // Assert
        val filteredMessage = outputFilterContext.outputMessage
        assertEquals("The date is [DATE].", filteredMessage.content)
    }

    @Test
    fun `should apply custom filter defined in DSL`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)
        val inputFilterContext = OutputFilterContext(
            context,
            Conversation(
                transcript = mutableListOf(
                    AssistantMessage(
                        content = "My SSN is 123-45-6789.",
                    ),
                ),
                user = User(id = "user-id"),
            ),
            Conversation(
                transcript = mutableListOf(
                    AssistantMessage(
                        content = "My SSN is 123-45-6789.",
                    ),
                ),
                user = User(id = "user-id"),
            ),
            systemPrompt = "system-prompt",
        )

        // Act
        inputFilterContext.guardrails {
            filter("custom") {
                replace("\\d{3}-\\d{2}-\\d{4}", "[SSN]")
            }
        }

        // Assert
        val filteredMessage = inputFilterContext.outputMessage
        assertEquals("My SSN is [SSN].", filteredMessage.content)
    }
}
