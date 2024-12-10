package ai.ancf.lmos.arc.api

/**
 * Short-hand function to create an agent request with a single user message.
 */
fun agentRequest(content: String, conversationId: String, vararg binaryData: BinaryData, turnId: String? = null) =
    AgentRequest(
        messages = listOf(
            Message(
                "user",
                content,
                turnId = turnId,
                binaryData = binaryData.toList(),
            ),
        ),
        conversationContext = ConversationContext(conversationId, turnId),
    )

/**
 * Short-hand functions to create a messages.
 */
fun userMessage(content: String, turnId: String? = null) = Message("user", content, turnId = turnId)

fun assistantMessage(content: String, turnId: String? = null, binaryData: List<BinaryData>? = null) = Message(
    "assistant",
    content,
    turnId = turnId,
    binaryData = binaryData,
)
