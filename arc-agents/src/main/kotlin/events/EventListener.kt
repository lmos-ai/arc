// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.events

/**
 * EventHandler interface.
 */
interface EventHandler<T : Event> {
    fun onEvent(event: T)
}
