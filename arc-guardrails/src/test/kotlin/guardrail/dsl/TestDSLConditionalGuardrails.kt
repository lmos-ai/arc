package ai.ancf.lmos.arc.guardrail.dsl

import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.OutputFilterContext
import ai.ancf.lmos.arc.guardrail.extensions.applyGuardrails
import ai.ancf.lmos.arc.guardrail.filters.Condition
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestDSLConditionalGuardrails {

    @Test
    fun `should apply guardrails conditionally based on message content`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)

        val inputFilterContext = OutputFilterContext(
            context,
            Conversation(
                transcript = mutableListOf(
                    AssistantMessage(
                        content = "Please help me.",
                    ),
                ),
                user = User(id = "user-id"),
            ),
            Conversation(
                transcript = mutableListOf(
                    AssistantMessage(
                        content = "Please help me.",
                    ),
                ),
                user = User(id = "user-id"),
            ),
            systemPrompt = "system-prompt",
        )

        // Act
        inputFilterContext.applyGuardrails {
            `if`(Condition.Equals("Please help me.")) {
                then {
                    length(5)
                }
                `else` {
                    length(10)
                }
            }
        }

        // Assert
        val filteredMessage = inputFilterContext.outputMessage
        assertEquals("Pleas", filteredMessage.content)
    }
}
