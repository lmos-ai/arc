// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.events

/**
 * EventHandler interface.
 */
fun interface EventHandler<T : Event> {
    fun onEvent(event: T)
}
