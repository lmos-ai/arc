// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.graphql

import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.core.Result

interface ErrorHandler {

    fun handleError(e: Exception): Result<AssistantMessage, Exception>
}
