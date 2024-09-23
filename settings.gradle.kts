plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

rootProject.name = "arc"

include("arc-scripting")
include("arc-azure-client")
include("arc-ollama-client")
include("arc-gemini-client")
include("arc-reader-html")
include("arc-reader-pdf")
include("arc-result")
include("arc-agents")
include("arc-spring-boot-starter")
include("arc-memory-mongo-spring-boot-starter")
include("arc-spring-ai")
include("arc-api")
include("arc-agent-client")
include("arc-graphql-spring-boot-starter")
include("arc-bom")
include("arc-runner")
include("arc-guardrails")
