// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.memory.redis

/**
 * Entry in memory.
 */
data class MemoryEntry<T>(val value: T)

data class MemoryKey(val owner: String, val key: String, val sessionId: String? = null)
