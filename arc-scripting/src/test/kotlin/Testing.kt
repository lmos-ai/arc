// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.scripting

/**
 * Reads a script from the resource folder.
 */
fun readScript(name: String) = Thread.currentThread().contextClassLoader.getResourceAsStream(name).use {
    String(it!!.readAllBytes())
}
