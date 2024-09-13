package ai.ancf.lmos.arc.client.azure

import com.azure.ai.openai.models.FunctionDefinition
import com.azure.core.util.BinaryData
import ai.ancf.lmos.arc.agents.functions.LLMFunction
import ai.ancf.lmos.arc.agents.functions.ParameterSchema
import ai.ancf.lmos.arc.agents.functions.ParametersSchema
import kotlinx.serialization.json.*


object AzureOpenAIFunctionAdaptor {

    fun toAzureOpenAIFunction(fn: LLMFunction) = FunctionDefinition(fn.name).apply {
        description = fn.description
        parameters = BinaryData.fromObject( jsonObjectToMap(fn.parameters.toAzureOpenAIFormat()))
    }

    /**
     * Extension functions for ParametersSchema to convert it in OpenAPI format.
     */
    private fun ParametersSchema.toAzureOpenAIFormat(): JsonObject {
        val properties = parameters.associate { it.name to it.toAzureOpenAIFormat() as JsonElement }
        return buildJsonObject {
            put("type", JsonPrimitive("object"))
            put("required", JsonArray(required.map { JsonPrimitive(it) }))
            put("properties", JsonObject(properties))
        }
    }

    private fun ParameterSchema.toAzureOpenAIFormat(): JsonObject {
        return buildJsonObject {
            put("type", JsonPrimitive(type.schemaType))

            if (description.isNotEmpty()) {
                put("description", JsonPrimitive(description))
            }

            if (type.schemaType == "array" && type.items != null) {
                val itemsObject = buildJsonObject {
                    put("type", JsonPrimitive(type.items!!.schemaType))

                    if (type.items!!.schemaType == "object" && type.items!!.properties != null) {
                        val properties = type.items!!.properties?.associate { it.name to it.toAzureOpenAIFormat() }
                        properties?.let { JsonObject(it) }?.let { put("properties", it) }
                    }
                }
                put("items", itemsObject)
            }

            if (type.schemaType == "object" && type.properties != null) {
                val properties = type.properties!!.associate { it.name to it.toAzureOpenAIFormat() }
                put("properties", JsonObject(properties))
            }

            if (enum.isNotEmpty()) {
                put("enum", JsonArray(enum.map { JsonPrimitive(it) }))
            }
        }
    }



    private fun jsonObjectToMap(jsonObject: JsonObject): Map<String, Any?> {
        return jsonObject.mapValues { (_, value) ->
            when (value) {
                is JsonPrimitive -> {
                    if (value.isString) value.content
                    else value.booleanOrNull ?: value.intOrNull ?: value.floatOrNull ?: value.doubleOrNull
                }
                is JsonArray -> {
                    value.map { jsonElement ->
                        when (jsonElement) {
                            is JsonObject -> jsonObjectToMap(jsonElement)
                            is JsonPrimitive -> if (jsonElement.isString) jsonElement.content else jsonElement.booleanOrNull
                                ?: jsonElement.intOrNull ?: jsonElement.floatOrNull ?: jsonElement.doubleOrNull
                            else -> null
                        }
                    }
                }
                is JsonObject -> {
                    jsonObjectToMap(value)
                }
                else -> null
            }
        }
    }
}