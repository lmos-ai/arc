// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.events

import java.time.Instant

/**
 * Interface for all event types.
 */
interface Event {
    val timestamp: Instant
}
