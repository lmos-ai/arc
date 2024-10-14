package ai.ancf.lmos.arc.guardrail.dsl

import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.InputFilterContext
import ai.ancf.lmos.arc.agents.dsl.extensions.InterruptProcessingException
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import ai.ancf.lmos.arc.agents.dsl.get
import ai.ancf.lmos.arc.agents.memory.InMemoryMemory
import ai.ancf.lmos.arc.agents.memory.Memory
import ai.ancf.lmos.arc.guardrail.extensions.guardrails
import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TestDSLValidateContextGuardrails {

    @Test
    fun `should throw exception because context variable in missing in the context`(): Unit = runTest {
        val context = spyk<DSLContext>()
        coEvery { context.get<Conversation>() } returns Conversation(
            user = User("user"),
        )

        coEvery { context.get<Memory>() } returns InMemoryMemory()

        val message = UserMessage(
            content = "This message is too long.",
        )

        val inputFilterContext = InputFilterContext(
            context,
            Conversation(
                transcript = mutableListOf(message),
                user = User(id = "user"),
            ),
        )

        val contextVariableName = "contextVariableName"

        assertThrows<InterruptProcessingException> {
            inputFilterContext.guardrails {
                validateContext(contextVariableName)
            }
        }
    }

    @Test
    fun `should not throw exception because context variable is present in the context`(): Unit = runTest {
        val context = spyk<DSLContext>()
        val contextVariableName = "contextVariableName"

        coEvery { context.get<Conversation>() } returns Conversation(
            user = User("user"),
        )

        coEvery { context.get<Memory>() } returns InMemoryMemory()

        context.memory(contextVariableName, "value")

        val message = UserMessage(
            content = "This message is too long.",
        )

        val inputFilterContext = InputFilterContext(
            context,
            Conversation(
                transcript = mutableListOf(message),
                user = User(id = "user"),
            ),
        )

        assertDoesNotThrow {
            inputFilterContext.guardrails {
                validateContext(contextVariableName)
            }
        }
    }
}
