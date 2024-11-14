// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.InterruptProcessingException
import ai.ancf.lmos.arc.agents.dsl.get
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class WhitelistValidationFilterTest {

    @Test
    fun `should allow message if all disallowed term is in whitelist`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)

        val message = UserMessage(
            content = "allowed terms message",
        )
        val disallowedPattern = "allowed"
        val whitelist = listOf("allowed", "terms", "message")
        val filter = WhitelistValidationFilter(context, disallowedPattern, whitelist, "Disallowed term detected.")

        // Act
        val result = filter.filter(message)

        // Assert
        assertEquals(message, result)
    }

    @Test
    fun `should throw exception if message contains disallowed terms and not present in whitelist`() = runTest {
        // Arrange
        val context = mockk<DSLContext>(relaxed = true)

        val message = UserMessage(
            content = "cool terms forbidden",
        )
        coEvery { context.get<Conversation>() } returns Conversation(
            User("user"),
            transcript = listOf(message),
        )
        val disallowedPattern = "cool"
        val whitelist = listOf("terms")
        val filter = WhitelistValidationFilter(context, disallowedPattern, whitelist, "Disallowed term detected.")

        // Act & Assert
        assertThrows<InterruptProcessingException> {
            filter.filter(message)
        }
    }
}
