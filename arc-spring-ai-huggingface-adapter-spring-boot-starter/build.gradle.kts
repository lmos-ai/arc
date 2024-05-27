// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    implementation(project(":arc-agents"))

    implementation(project(":arc-result"))
    // Logging
    implementation("org.slf4j:slf4j-api:1.7.25")

    // spring.ai
    implementation("org.springframework.ai:spring-ai-huggingface:0.8.1-SNAPSHOT")
}
repositories {
    mavenCentral()
    google()
    maven("https://repo.spring.io/milestone")
    maven("https://repo.spring.io/snapshot")
}
