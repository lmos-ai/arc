// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.functions

import ai.ancf.lmos.arc.agents.dsl.DSLContext

/**
 * Tags a function that requires a DSLContext to be executed.
 */
interface FunctionWithContext {

    /**
     * Returns a new instance of the function that executes with the given context.
     */
    fun withContext(context: DSLContext): LLMFunction
}
