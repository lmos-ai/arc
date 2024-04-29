// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.functions

/**
 * Provides LLMFunctions.
 * Usually there is one instance of this class per application.
 */
interface LLMFunctionProvider {

    fun provide(functionName: String): List<LLMFunction>
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

    override fun provide(functionName: String): List<LLMFunction> {
        return functions().filter { it.name == functionName }
    }

    private fun functions() = loaders.flatMap { it.load() } + functions
}
