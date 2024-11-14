// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    val ktorVersion = "3.0.1"

    // Ktor
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")

    implementation(project(":arc-api"))

    testImplementation(project(":arc-result"))
    testImplementation(project(":arc-agents"))
    testImplementation(project(":arc-graphql-spring-boot-starter"))
    testImplementation(project(":arc-spring-boot-starter"))
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.3")
    testImplementation("org.springframework.boot:spring-boot-starter:3.3.3")
}
