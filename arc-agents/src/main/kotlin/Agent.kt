// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents

import io.github.lmos.arc.core.Result

/**
 * The main Agent interface.
 */
interface Agent<I, O> {

    val name: String

    val description: String

    suspend fun execute(input: I): Result<O, AgentFailedException>
}

/**
 * Exception thrown when an agent fails to execute.
 */
open class AgentFailedException(msg: String, cause: Exception? = null) : Exception(msg, cause)

/**
 * Exception thrown when an agent does not execute.
 * Usually thrown when the input has been completely filtered out or some other condition is not met.
 */
class AgentNotExecutedException(msg: String, cause: Exception? = null) : AgentFailedException(msg, cause)
