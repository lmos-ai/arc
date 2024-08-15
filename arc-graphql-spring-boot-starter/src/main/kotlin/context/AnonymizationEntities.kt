// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.graphql.context

import io.github.lmos.arc.agents.conversation.AnonymizationEntity
import java.util.concurrent.atomic.AtomicReference

class AnonymizationEntities(anonymizationEntities: List<AnonymizationEntity>) {
    private val _entities = AtomicReference(anonymizationEntities)
    var entities: List<AnonymizationEntity>
        get() = _entities.get()
        set(value) {
            _entities.set(value)
        }
}
