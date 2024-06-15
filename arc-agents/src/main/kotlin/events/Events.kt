// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.events

import java.time.Instant

/**
 * Interface for all event types.
 */
abstract class Event {
    abstract val timestamp: Instant
}
