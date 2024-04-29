// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.functions

import io.github.lmos.arc.core.Result

/**
 * Describes a function that can be passed to a Large Language Model.
 */
interface LLMFunction {
    val name: String
    val parameters: ParametersSchema
    val description: String
    val group: String?
    val isSensitive: Boolean

    suspend fun execute(input: Map<String, Any?>): Result<String, LLMFunctionException>
}

/**
 * Exceptions thrown when an LLMFunction fails.
 */
class LLMFunctionException(msg: String, override val cause: Exception? = null) : Exception(msg, cause)

/**
 * Schema tha describes LLM Functions parameters.
 */
data class ParametersSchema(val required: List<String>, val parameters: List<ParameterSchema>) {
    fun asMap() = mapOf(
        "type" to "object",
        "required" to required,
        "properties" to parameters.associate { it.name to it.asMap() },
    )
}

/**
 * Schema that describes a single parameter of an LLM Function.
 */
data class ParameterSchema(
    val name: String,
    val description: String,
    val type: ParameterType,
    val enum: List<String>,
) {
    fun asMap() = mapOf(
        "type" to type.schemaType,
        "description" to description,
    ).let {
        if (enum.isNotEmpty()) {
            it + ("enum" to enum)
        } else {
            it
        }
    }
}

/**
 * A parameter type that can be used by LLM Functions.
 */
class ParameterType(val schemaType: String)
