// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.assistants.support

import ai.ancf.lmos.arc.agents.conversation.ConversationClassification

object Unresolved : ConversationClassification {
    override fun toString() = "UNRESOLVED"
}
