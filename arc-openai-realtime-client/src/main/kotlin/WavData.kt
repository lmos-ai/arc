package ai.ancf.lmos.arc.agent.client.ws

import ai.ancf.lmos.arc.agents.conversation.BinaryData
import ai.ancf.lmos.arc.agents.conversation.DataStream

/**
 * Creates a [BinaryData] object with the given wav audio data.
 */
fun wavData(audio: DataStream) = BinaryData(mimeType = "audio/wav", stream = audio)