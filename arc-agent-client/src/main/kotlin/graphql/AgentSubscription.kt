// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.agent.client.graphql

/**
 * GraphQL subscription for Agent messages.
 */
internal const val AGENT_SUBSCRIPTION = """
subscription(${'$'}request: AgentRequestInput!, ${'$'}agentName: String) {
  agent(agentName: ${'$'}agentName, request: ${'$'}request) {
        status,
        responseTime,
        anonymizationEntities {
          type,
          value,
          replacement,
        },
        messages {
           content
           format,
           role,
        }
  }
}
"""
