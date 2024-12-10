package ai.ancf.lmos.arc.agent.client

import ai.ancf.lmos.arc.agent.client.ws.Session
import ai.ancf.lmos.arc.agent.client.ws.toJsonSchema
import ai.ancf.lmos.arc.agents.functions.LLMFunction
import ai.ancf.lmos.arc.agents.functions.LLMFunctionException
import ai.ancf.lmos.arc.agents.functions.ParameterSchema
import ai.ancf.lmos.arc.agents.functions.ParameterType
import ai.ancf.lmos.arc.agents.functions.ParametersSchema
import ai.ancf.lmos.arc.core.Result
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FunctionToSchemaTest {

    private val testFunction = object : LLMFunction {
        override val name: String = "test-name"
        override val parameters = ParametersSchema(
            parameters = listOf(
                ParameterSchema(
                    name = "country",
                    description = "The country to get the capital of",
                    type = ParameterType(schemaType = "string"),
                    enum = emptyList()
                )
            ), required = listOf("country")
        )
        override val description: String = "test-description"
        override val group: String? = null
        override val isSensitive: Boolean = false

        override suspend fun execute(input: Map<String, Any?>): Result<String, LLMFunctionException> {
            error("not implemented")
        }
    }

    @Test
    fun `test converting a simple function`() {
        val result = testFunction.toJsonSchema()
        assertThat(result.toString()).isEqualTo(
            """
            {"type":"function","name":"test-name","description":"test-description","parameters":{"type":"object","required":["country"],"properties":{"country":{"type":"string","description":"The country to get the capital of"}}}}
        """.trimIndent()
        )
    }

    @Test
    fun `test converting a function within a Session`() {
        val session = Session(tools = listOf(testFunction.toJsonSchema()))
        assertThat(Json.encodeToString(session)).isEqualTo(
            """
            {"tools":[{"type":"function","name":"test-name","description":"test-description","parameters":{"type":"object","required":["country"],"properties":{"country":{"type":"string","description":"The country to get the capital of"}}}}]}
        """.trimIndent()
        )
    }
}