// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.dsl.extensions

/**
 * Extensions to make using kotlin string templating easier.
 */

/**
 * Returns the string if the predicate is true.
 * For example,
 * """ Some string template ${someVariableThatResultToTrue then "Hello"} """
 * will return "Some string template Hello".
 * This is simply a shorthand for
 * """ Some string template ${if(someVariableThatResultToTrue) "Hello" else "" } """.
 */
infix fun Any?.then(out: String) = if(this == true) out else ""
