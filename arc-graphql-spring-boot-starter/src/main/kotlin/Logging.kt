package io.github.lmos.arc.graphql

import io.github.lmos.arc.api.AgentRequest
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.MDC

suspend fun <T> withLogContext(
    request: AgentRequest,
    block: suspend kotlinx.coroutines.CoroutineScope.() -> T,
): T {
    val current = MDC.getCopyOfContextMap() ?: emptyMap()
    val extraContext = mapOf(
        "conversationId" to request.conversationContext.conversationId,
    ) + request.systemContext.associate { it.key to it.value }
    return withContext(MDCContext(current + extraContext), block)
}

