// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.memory

suspend fun evaluateCondition(condition: Condition, message: ConversationMessage, context: DSLContext): Boolean {
    val content = message.content
    return when (condition) {
        is Condition.ContextEquals -> {
            val contextValue = context.memory(condition.contextVariableName) as String
            contextValue == condition.right
        }
        is Condition.ContextNotEquals -> {
            val contextValue = context.memory(condition.contextVariableName) as String
            contextValue != condition.right
        }
        is Condition.Equals -> content == condition.value
        is Condition.NotEquals -> content != condition.value
        is Condition.GreaterThan -> content.length > condition.number
        is Condition.LessThan -> content.length < condition.number
        is Condition.GreaterThanOrEqual -> content.length >= condition.number
        is Condition.LessThanOrEqual -> content.length <= condition.number
    }
}

sealed class Condition {
    data class ContextEquals(val contextVariableName: String, val right: String) : Condition()
    data class ContextNotEquals(val contextVariableName: String, val right: String) : Condition()
    data class Equals(val value: String) : Condition()
    data class NotEquals(val value: String) : Condition()
    data class GreaterThan(val number: Int) : Condition()
    data class LessThan(val number: Int) : Condition()
    data class GreaterThanOrEqual(val number: Int) : Condition()
    data class LessThanOrEqual(val number: Int) : Condition()
}

sealed class ErrorHandler {
    data class Log(val message: String) : ErrorHandler()
    data class Break(val message: String) : ErrorHandler()
}

data class Example(val input: String, val output: String)

sealed class ApiParam {
    data class Query(val name: String, val value: String) : ApiParam()
}
