// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.dsl.extensions

import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.get
import ai.ancf.lmos.arc.agents.events.Event
import ai.ancf.lmos.arc.agents.events.EventPublisher

/**
 * Extensions for eventing.
 */
suspend fun DSLContext.emit(event: Event) {
    get<EventPublisher>().publish(event)
}
