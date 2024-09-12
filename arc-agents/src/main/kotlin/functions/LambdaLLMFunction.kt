// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.functions

import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.core.Failure
import ai.ancf.lmos.arc.core.Result
import ai.ancf.lmos.arc.core.mapFailure
import ai.ancf.lmos.arc.core.result
import org.slf4j.LoggerFactory

/**
 * Bridges a kotlin function to the LLMFunction interface.
 */
data class LambdaLLMFunction(
    override val name: String,
    override val description: String,
    override val group: String?,
    override val isSensitive: Boolean,
    override val parameters: ParametersSchema,
    private val context: DSLContext,
    private val function: suspend DSLContext.(List<String?>) -> String,
) : LLMFunction, FunctionWithContext {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Executes the function and returns the result.
     */
    override suspend fun execute(input: Map<String, Any?>): Result<String, LLMFunctionException> {
        return try {
            log.debug("Calling function $name with $input")
            result<String, Exception> {
                function.invoke(context, parameters.parameters.map { p -> input[p.name]?.let { "$it" } })
            }.mapFailure { LLMFunctionException("LLMFunction call $name failed! ", it) }
        } catch (ex: Exception) {
            Failure(LLMFunctionException("LLMFunction call $name failed!", ex))
        }
    }

    override fun withContext(context: DSLContext): LLMFunction {
        return LambdaLLMFunction(name, description, group, isSensitive, parameters, context, function)
    }
}
