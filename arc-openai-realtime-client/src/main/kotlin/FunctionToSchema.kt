// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.agent.client.ws

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.eclipse.lmos.arc.agents.functions.LLMFunction
import org.eclipse.lmos.arc.agents.functions.ParameterSchema
import org.eclipse.lmos.arc.agents.functions.ParametersSchema

/**
 * Converts an LLMFunction to a JSON Schema.
 */
fun LLMFunction.toJsonSchema(): JsonObject {
    return buildJsonObject {
        put("type", JsonPrimitive("function"))
        put("name", JsonPrimitive(name))
        put("description", JsonPrimitive(description))
        put("parameters", parameters.toJsonSchema())
    }
}

/**
 * Converts a ParametersSchema to a JSON Schema.
 */
fun ParametersSchema.toJsonSchema(): JsonObject {
    val properties = parameters.associate { it.name to it.toJsonSchema() as JsonElement }
    return buildJsonObject {
        put("type", JsonPrimitive("object"))
        put("required", JsonArray(required.map { JsonPrimitive(it) }))
        put("properties", JsonObject(properties))
    }
}

/**
 * Extension functions for ParameterSchema to convert it to OpenAPI JSON format.
 */
private fun ParameterSchema.toJsonSchema(): JsonObject = buildJsonObject {
    put("type", JsonPrimitive(type.schemaType))

    if (description.isNotEmpty()) {
        put("description", JsonPrimitive(description))
    }

    when (type.schemaType) {
        "array" -> {
            val itemsObject = buildJsonObject {
                put("type", JsonPrimitive(type.items?.schemaType ?: "unknown"))

                val items = type.items
                if (items?.schemaType == "object") {
                    items.properties?.let { propertiesList ->
                        val properties = propertiesList.associate { it.name to it.toJsonSchema() }
                        put("properties", JsonObject(properties))
                    }
                }
            }
            put("items", itemsObject)
        }

        "object" -> {
            type.properties?.let { props ->
                val properties = props.associate { it.name to it.toJsonSchema() }
                put("properties", JsonObject(properties))
            }
        }
    }

    if (enum.isNotEmpty()) {
        put("enum", JsonArray(enum.map { JsonPrimitive(it) }))
    }
}
