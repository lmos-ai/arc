// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.spring

import ai.ancf.lmos.arc.agents.events.Event
import java.time.Instant

class TestEvent : Event {
    override val timestamp: Instant = Instant.now()
}
