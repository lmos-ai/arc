// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.graphql

import org.eclipse.lmos.arc.api.AgentRequest

interface ContextHandler {

    suspend fun <T> inject(request: AgentRequest, block: suspend (Set<Any>) -> T): T
}

class EmptyContextHandler : ContextHandler {
    override suspend fun <T> inject(request: AgentRequest, block: suspend (Set<Any>) -> T): T {
        return block(emptySet())
    }
}
