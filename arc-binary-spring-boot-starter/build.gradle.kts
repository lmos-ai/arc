// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

dependencies {

    implementation(project(":arc-agents"))
    implementation(project(":arc-result"))
    implementation(project(":arc-api"))
    implementation(project(":arc-agent-client"))

    // Spring
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.websocket)

    // Test
    testImplementation(project(":arc-spring-boot-starter"))
    testImplementation(project(":arc-agent-client"))
    testImplementation(libs.spring.boot.starter.test)
}
