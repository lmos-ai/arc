// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.functions

import ai.ancf.lmos.arc.core.Result
import kotlinx.serialization.Serializable

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
@Serializable
data class ParametersSchema(val required: List<String>, val parameters: List<ParameterSchema>)

/**
 * Schema that describes a single parameter of an LLM Function.
 */
@Serializable
data class ParameterSchema(
    val name: String,
    val description: String,
    val type: ParameterType,
    val enum: List<String>,
)

/**
 * A parameter type that can be used by LLM Functions.
 */

@Serializable
class ParameterType(
    val schemaType: String,
    val items: ParameterType? = null,
    val properties: List<ParameterSchema>? = null,
)
