// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agent.client.graphql

/**
 * GraphQL subscription for Agent messages.
 */
internal const val AGENT_SUBSCRIPTION = """
subscription(${'$'}request: AgentRequestInput!) {
  agent(request: ${'$'}request) {
        anonymizationEntities {
          type
          value
          replacement
        }
        messages {
           content
        }
  }
}
"""
