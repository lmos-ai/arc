// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.assistants.support.usecases

fun extractVersion(message: String): String? {
    val versionRegex = "<Version:(.*)>".toRegex(RegexOption.IGNORE_CASE)
    return versionRegex.find(message)?.groupValues?.elementAtOrNull(1)?.trim()
}
