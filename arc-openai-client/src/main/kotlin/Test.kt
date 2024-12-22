import ai.ancf.lmos.arc.agents.functions.*
import ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings
import ai.ancf.lmos.arc.core.Result
import ai.ancf.lmos.arc.core.Success
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.core.JsonValue
import com.openai.models.*
import java.util.*


fun main() {

    val client = OpenAIOkHttpClient.builder()
        .apiKey("")
        .build()

    val functionArgs = listOf(
    ParameterSchema(
        name = "location",
        description = "The city and state, e.g., San Francisco, CA.",
        type = ParameterType("string"),
        enum = emptyList(),
    )

   ,ParameterSchema(
        name = "unit",
        description = "The temperature unit to use.",
        type = ParameterType("string"),
        enum = listOf("celsius", "fahrenheit"),
    ))

    val parameters = ParametersSchema(
        required = listOf("location", "unit"),
        parameters = functionArgs
    )

    val functions = listOf(
        object : LLMFunction {
            override val name: String
                get() = "get_current_weather"
            override val parameters: ParametersSchema
                get() = ParametersSchema(
                    listOf("location", "unit"),
                    listOf(
                        ParameterSchema(
                            name = "location",
                            description = "The city and state, e.g., San Francisco, CA.",
                            type = ParameterType("string"),
                            enum = emptyList(),
                        )

                        ,ParameterSchema(
                            name = "unit",
                            description = "The temperature unit to use.",
                            type = ParameterType("string"),
                            enum = listOf("celsius", "fahrenheit"),
                        )
                    )
                )
            override val description: String
                get() = "Retrieve the current weather for a specified location."
            override val group: String?
                get() = null
            override val isSensitive: Boolean
                get() = false

            override suspend fun execute(input: Map<String, Any?>): Result<String, LLMFunctionException> {
                return Success("test")
            }
        },
        object : LLMFunction {
            override val name: String
                get() = "dummy"
            override val parameters: ParametersSchema
                get() = ParametersSchema(
                    emptyList(),
                    listOf(
                        ParameterSchema(
                            name = "location",
                            description = "The city and state, e.g., San Francisco, CA.",
                            type = ParameterType("string"),
                            enum = emptyList(),
                        )
                    )
                )
            override val description: String
                get() = "Dummy test function."
            override val group: String?
                get() = null
            override val isSensitive: Boolean
                get() = false

            override suspend fun execute(input: Map<String, Any?>): Result<String, LLMFunctionException> {
                return Success("test")
            }
        }
    )

    val settings = ChatCompletionSettings()

    val params = ChatCompletionCreateParams.builder()
        .messages(
            listOf(
                ChatCompletionMessageParam.ofChatCompletionDeveloperMessageParam(
                    ChatCompletionDeveloperMessageParam.builder()
                        .role(ChatCompletionDeveloperMessageParam.Role.DEVELOPER)
                        .content(ChatCompletionDeveloperMessageParam.Content.ofTextContent("You are a helpful assistant."))
                        .build()
                ),
                ChatCompletionMessageParam.ofChatCompletionUserMessageParam(
                    ChatCompletionUserMessageParam.builder()
                        .role(ChatCompletionUserMessageParam.Role.USER)
                        .content(ChatCompletionUserMessageParam.Content.ofTextContent("What's the weather like in New York today?"))
                        .build()
                )
            )
        )
        .functions(toOpenAIFunctions(functions) ?: emptyList())
        .model(ChatModel.GPT_4O_MINI)
        .apply {
            settings.temperature?.let { temperature(it) }
        }
        .build()

    val chatCompletion: ChatCompletion = client.chat().completions().create(params)
    println(chatCompletion)
    val message = chatCompletion.choices().get(0).message()
    val finishReason = chatCompletion.choices().get(0).finishReason()
    println(message)
    println(finishReason)
}


    private fun toOpenAIFunctions(functions: List<LLMFunction>) = functions.map { fn ->
        val map = fn.parameters.parameters.associate { param ->
            param.name to mapOf(
                "type" to param.type.schemaType,
                "description" to param.description,
                "enum" to param.enum
            )
        }
        ChatCompletionCreateParams.Function.builder().name(fn.name)
            .description(fn.description)
            .parameters(
                FunctionParameters.builder().putAdditionalProperty("type", JsonValue.from("object"))
                    .putAdditionalProperty("properties", JsonValue.from(map))
                    .putAdditionalProperty("required", JsonValue.from(fn.parameters.required)).build()
            )
            .build()
    }.takeIf { it.isNotEmpty() }