package ai.ancf.lmos.arc.agent.client

import ai.ancf.lmos.arc.api.AgentRequest
import ai.ancf.lmos.arc.api.BinaryData
import ai.ancf.lmos.arc.api.ConversationContext
import ai.ancf.lmos.arc.api.Message
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
    message: String = "The input from the user."
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
                            data = Base64.encode(data),
                            mimeType = mimeType.value
                        )
                    )
                )
            )
        )
    )
