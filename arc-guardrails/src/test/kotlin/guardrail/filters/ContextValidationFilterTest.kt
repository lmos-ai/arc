package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.InterruptProcessingException
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import ai.ancf.lmos.arc.agents.dsl.get
import ai.ancf.lmos.arc.agents.memory.InMemoryMemory
import ai.ancf.lmos.arc.agents.memory.Memory
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class ContextValidationFilterTest {

    @Test
    fun `should throw error if key not found in context`() {
        val context = spyk<DSLContext>()
        coEvery { context.get<Conversation>() } returns Conversation(
            user = User("user"),
        )

        coEvery { context.get<Memory>() } returns InMemoryMemory()

        val contextVariableName = "contextVariableName"
        val message = mockk<ConversationMessage>()
        val filter = ContextValidationFilter(context, contextVariableName)

        assertThrows<InterruptProcessingException> { runBlocking { filter.filter(message) } }
    }

    @Test
    fun `should return message if key found in context`() {
        val context = spyk<DSLContext>()
        coEvery { context.get<Conversation>() } returns Conversation(
            user = User("user"),
        )

        coEvery { context.get<Memory>() } returns InMemoryMemory()

        val contextVariableName = "contextVariableName"
        runBlocking { context.memory(contextVariableName, "value") }
        val message = mockk<ConversationMessage>()
        val filter = ContextValidationFilter(context, contextVariableName)

        assertDoesNotThrow { runBlocking { filter.filter(message) } }
    }
}
