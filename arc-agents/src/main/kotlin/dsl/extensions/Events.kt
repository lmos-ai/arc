// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.dsl.extensions

import io.github.lmos.arc.agents.dsl.DSLContext
import io.github.lmos.arc.agents.dsl.get
import io.github.lmos.arc.agents.events.Event
import io.github.lmos.arc.agents.events.EventPublisher

/**
 * Extensions for eventing.
 */
suspend fun DSLContext.emit(event: Event) {
    get<EventPublisher>().publish(event)
}
