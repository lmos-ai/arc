// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.assistants.support.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import ai.ancf.lmos.arc.assistants.support.usecases.extractUseCaseId
import org.slf4j.LoggerFactory

/**
 * An output filter that handles responses from the LLM that contain a use case id.
 * For example, "<ID:useCaseId>"
 */
context(DSLContext)
class UseCaseResponseHandler : AgentFilter {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        val (text, useCaseId) = extractUseCaseId(message.content)

        log.info("Use case: $useCaseId used")
        val usedUseCases = memory("usedUseCases") as List<String>? ?: emptyList()
        log.info("All Use cases used: $usedUseCases")
        memory("usedUseCases", usedUseCases + useCaseId)

        return message.update(text)
    }
}
