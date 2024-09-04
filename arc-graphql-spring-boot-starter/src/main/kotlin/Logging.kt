// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.graphql

import ai.ancf.lmos.arc.api.AgentRequest
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.MDC

suspend fun <T> withLogContext(
    agent: String,
    request: AgentRequest,
    block: suspend kotlinx.coroutines.CoroutineScope.() -> T,
): T {
    val current = MDC.getCopyOfContextMap() ?: emptyMap()
    val extraContext = mapOf(
        "agent" to agent,
        "conversationId" to request.conversationContext.conversationId,
    ) + request.systemContext.associate { it.key to it.value }
    return withContext(MDCContext(current + extraContext), block)
}
