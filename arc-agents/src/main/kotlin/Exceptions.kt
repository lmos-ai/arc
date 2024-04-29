// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents

/**
 * Indicates that calling the Azure client has failed.
 * In the case of Hallucinations exceptions, the cause field will contain a HallucinationDetectedException.
 */
open class ArcException(msg: String = "Unexpected error!", override val cause: Exception? = null) : Exception(msg, cause)

/**
 * Indicates that the AI has performed incorrectly or unexpectedly.
 */
class HallucinationDetectedException(msg: String) : ArcException(msg)

/**
 * Indicates that a requested feature is not supported by the Model.
 */
class FeatureNotSupportedException(msg: String) : ArcException(msg)

/**
 * Indicates that the Model endpoint is not reachable.
 */
class ServerException(msg: String) : ArcException(msg)

/**
 * Indicates that the Model endpoint could not be called due to an authentication error.
 */
class AuthenticationException(msg: String) : ArcException(msg)

/**
 * Indicates that the provided settings are invalid.
 */
class InvalidSettingsException(msg: String) : ArcException(msg)
