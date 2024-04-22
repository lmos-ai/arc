// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents

/**
 * Indicates that calling the Azure client has failed.
 * In the case of Hallucinations exceptions, the cause field will contain a HallucinationDetectedException.
 */
open class AIException(msg: String = "Unexpected error!", override val cause: Exception? = null) : Exception(msg, cause)

/**
 * Indicates that the AI has performed incorrectly or unexpectedly.
 */
class HallucinationDetectedException(msg: String) : AIException(msg)

/**
 * Indicates that a requested feature is not supported by the Model.
 */
class FeatureNotSupportedException(msg: String) : AIException(msg)

/**
 * Indicates that the Model endpoint is not reachable.
 */
class ServerException(msg: String) : AIException(msg)

/**
 * Indicates that the provided settings are invalid.
 */
class InvalidSettingsException(msg: String) : AIException(msg)
