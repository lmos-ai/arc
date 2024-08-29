// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.functions

import ai.ancf.lmos.arc.agents.FunctionNotFoundException
import ai.ancf.lmos.arc.core.Failure
import ai.ancf.lmos.arc.core.Result
import ai.ancf.lmos.arc.core.Success

/**
 * Provides LLMFunctions.
 * Usually there is one instance of this class per application.
 */
interface LLMFunctionProvider {

    fun provide(functionName: String): Result<LLMFunction, FunctionNotFoundException>
}

/**
 * Loads Functions.
 * Typically, a [LLMFunctionProvider] uses [LLMFunctionLoader]s to load Arc Functions from different sources.
 * There can be many implementations of [LLMFunctionLoader]s in an application.
 */
fun interface LLMFunctionLoader {

    fun load(): List<LLMFunction>
}

/**
 * Implementation of the [LLMFunctionProvider] that combines multiple [LLMFunctionLoader]s and a list of [LLMFunction]s.
 */
class CompositeLLMFunctionProvider(
    private val loaders: List<LLMFunctionLoader>,
    private val functions: List<LLMFunction>,
) : LLMFunctionProvider {

    /**
     * Retrieves a list of LLMFunctions matching the given function name.
     *
     * @param functionName The name of the function to search for.
     * @return List of LLMFunctions matching the function name.
     * @throws NoSuchElementException if no matching LLMFunction is found.
     */

    override fun provide(functionName: String): Result<LLMFunction, FunctionNotFoundException> =
        functions().firstOrNull { it.name == functionName }?.let { Success(it) }
            ?: Failure(FunctionNotFoundException("No matching LLMFunction found for name: $functionName"))

    private fun functions() = loaders.flatMap { it.load() } + functions
}
