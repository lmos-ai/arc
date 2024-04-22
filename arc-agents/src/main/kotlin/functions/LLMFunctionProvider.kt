// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.functions

/**
 * Provides LLMFunctions.
 * Usually there is one instance of this class per application.
 */
fun interface LLMFunctionProvider {

    fun provideByGroup(functionGroup: String): List<LLMFunction>
}

/**
 * Loads Agents.
 * Typically, a [LLMFunctionProvider] uses [LLMFunctionLoader]s to load Arc Functions from different sources.
 * There can be many implementations of [LLMFunctionLoader]s in an application.
 */
fun interface LLMFunctionLoader {

    fun provideByGroup(functionGroup: String): List<LLMFunction>
}

/**
 * Implementation of the [LLMFunctionProvider] that combines multiple [LLMFunctionLoader]s and a list of [LLMFunction]s.
 */
class CompositeLLMFunctionProvider(
    private val loaders: List<LLMFunctionLoader>,
    private val functions: List<LLMFunction>,
) : LLMFunctionProvider {

    override fun provideByGroup(functionGroup: String): List<LLMFunction> {
        return loaders.flatMap { it.provideByGroup(functionGroup) } + functions
    }
}
