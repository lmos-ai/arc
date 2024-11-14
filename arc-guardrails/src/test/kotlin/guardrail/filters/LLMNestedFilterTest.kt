// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.llm
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import ai.ancf.lmos.arc.core.Success
import ai.ancf.lmos.arc.guardrail.dsl.LlmBodyBuilder
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LlmNestedFilterTest {

    @Test
    fun `should interact with LLM and apply nested filters`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)
        val body = LlmBodyBuilder().apply {
            userMessage("Check for sensitive content")
            systemMessage("System prompt")
        }
        val nestedFilter = LengthFilter(10)
        val filter = LlmNestedFilter(body, listOf(nestedFilter), context)
        val message = UserMessage(
            content = "This is a test message.",
            turnId = "123",
        )
        val llmResponse = AssistantMessage(content = "LLM response")

        coEvery {
            context.llm(
                userMessage = "Check for sensitive content",
                systemMessage = "System prompt",
                settings = any(),
                model = any(),
            )
        } returns Success(llmResponse)

        coEvery { context.memory(any(), any()) } just Runs

        // Act
        val result = filter.filter(message)

        // Assert
        val expectedContent = "This is a "
        assertEquals(expectedContent, result?.content)
        coVerify { context.memory("llm_response_123", llmResponse) }
    }
}
