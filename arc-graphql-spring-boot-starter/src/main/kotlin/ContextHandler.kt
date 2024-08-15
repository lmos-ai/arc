// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.graphql

import io.github.lmos.arc.api.AgentRequest

interface ContextHandler {

    suspend fun <T> inject(request: AgentRequest, block: suspend () -> T): T
}

class EmptyContextHandler : ContextHandler {
    override suspend fun <T> inject(request: AgentRequest, block: suspend () -> T): T {
        return block()
    }
}
