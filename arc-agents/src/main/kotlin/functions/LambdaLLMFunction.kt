// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.functions

import io.github.lmos.arc.agents.dsl.BasicDSLContext
import io.github.lmos.arc.agents.dsl.BeanProvider
import io.github.lmos.arc.agents.dsl.DSLContext
import io.github.lmos.arc.core.Failure
import io.github.lmos.arc.core.Result
import io.github.lmos.arc.core.mapFailure
import io.github.lmos.arc.core.result
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
    private val beanProvider: BeanProvider,
    private val function: suspend DSLContext.(List<String?>) -> String,
) : LLMFunction {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Executes the function and returns the result.
     */
    override suspend fun execute(input: Map<String, Any?>): Result<String, LLMFunctionException> {
        return try {
            log.debug("Calling function $name with $input")
            val functionContext = BasicDSLContext(beanProvider)
            result<String, Exception> {
                function.invoke(
                    functionContext,
                    parameters.parameters.map { p -> input[p.name]?.let { "$it" } },
                )
            }.mapFailure { LLMFunctionException("LLMFunction call $name failed! ", it) }
        } catch (ex: Exception) {
            Failure(LLMFunctionException("LLMFunction call $name failed!", ex))
        }
    }
}
