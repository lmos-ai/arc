// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.api

import kotlinx.serialization.Serializable

@Serializable
data class AnonymizationEntity(val type: String, val value: String, val replacement: String)
