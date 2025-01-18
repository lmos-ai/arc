package org.eclipse.lmos.arc.agent.client

import org.eclipse.lmos.arc.api.AgentRequest
import org.eclipse.lmos.arc.api.BinaryData
import org.eclipse.lmos.arc.api.ConversationContext
import org.eclipse.lmos.arc.api.Message
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Create a request containing a user message with binary data.
 */
@OptIn(ExperimentalEncodingApi::class)
suspend fun AgentClient.callWithAudio(
    conversationId: String,
    data: ByteArray,
    mimeType: AudioMimeType,
    message: String = "The input from the user.",
) =
    callAgent(
        AgentRequest(
            conversationContext = ConversationContext(conversationId),
            messages = listOf(
                Message(
                    role = "user",
                    content = message,
                    binaryData = listOf(
                        BinaryData(
                            dataAsBase64 = Base64.encode(data),
                            mimeType = mimeType.value,
                        ),
                    ),
                ),
            ),
        ),
    )
