package ai.ancf.lmos.arc.agent.client.ws

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseCreate(
    val response: ResponseData? = null,
    val type: String = "response.create"
)

@Serializable
data class ResponseData(
    val modalities: List<String>,
    val instructions: String
)

@Serializable
data class ConversationItemCreate(
    val item: Item,
    val type: String = "conversation.item.create"
)

@Serializable
data class Item(
    val type: String,
    val role: String,
    val content: List<Content>
)

@Serializable
data class Content(
    val type: String,
    val text: String? = null
)

@Serializable
data class ResponseDone(
    @SerialName("event_id") val eventId: String,
    val type: String,
    val response: Response
)

@Serializable
data class Response(
    val id: String,
    val status: String,
    val output: List<Output>,
    val usage: Usage
)

@Serializable
data class Output(
    val id: String,
    val type: String,
    val status: String,
    val role: String,
    val content: List<Content>
)

@Serializable
data class Usage(
    @SerialName("total_tokens") val totalTokens: Int,
    @SerialName("input_tokens") val inputTokens: Int,
    @SerialName("output_tokens") val outputTokens: Int,
    @SerialName("input_token_details") val inputTokenDetails: InputTokenDetails,
    @SerialName("output_token_details") val outputTokenDetails: OutputTokenDetails
)

@Serializable
data class InputTokenDetails(
    val cachedTokens: Int? = null,
    @SerialName("text_tokens") val textTokens: Int,
    @SerialName("audio_tokens") val audioTokens: Int,
    @SerialName("cached_tokens_details") val cachedTokensDetails: CachedTokensDetails
)

@Serializable
data class CachedTokensDetails(
    @SerialName("text_tokens") val textTokens: Int,
    @SerialName("audio_tokens") val audioTokens: Int
)

@Serializable
data class OutputTokenDetails(
    @SerialName("text_tokens") val textTokens: Int,
    @SerialName("audio_tokens") val audioTokens: Int
)

@Serializable
data class AudioTranscriptDoneEvent(
    @SerialName("event_id") val eventId: String,
    @SerialName("type") val type: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("content_index") val contentIndex: Int,
    @SerialName("transcript") val transcript: String
)