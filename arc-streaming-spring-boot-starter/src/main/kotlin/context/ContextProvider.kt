// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.ws.context

import ai.ancf.lmos.arc.agents.dsl.extensions.SystemContext
import ai.ancf.lmos.arc.agents.dsl.extensions.SystemContextProvider
import ai.ancf.lmos.arc.agents.dsl.extensions.UserProfile
import ai.ancf.lmos.arc.agents.dsl.extensions.UserProfileProvider
import ai.ancf.lmos.arc.api.AgentRequest

/**
 * Provides the system context and user profile to the context of the DSL.
 */
data class ContextProvider(val request: AgentRequest) : SystemContextProvider, UserProfileProvider {

    override fun provideSystem(): SystemContext {
        return SystemContext(request.systemContext?.associate { it.key to it.value } ?: emptyMap())
    }

    override fun provideProfile(): UserProfile {
        return UserProfile(request.userContext?.profile?.associate { it.key to it.value } ?: emptyMap())
    }
}
