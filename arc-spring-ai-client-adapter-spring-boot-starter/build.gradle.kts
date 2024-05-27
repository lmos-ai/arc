// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    implementation(project(":arc-agents"))

    implementation(project(":arc-result"))
    // Logging
    implementation("org.slf4j:slf4j-api:1.7.25")

    // spring.ai
    implementation("io.springboot.ai:spring-ai-core:1.0.3")
}
repositories {
    mavenCentral()
}
