// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    api(project(":arc-result"))
    api(project(":arc-agents"))
    implementation(project(":arc-scripting"))

    compileOnly(project(":arc-azure-client"))
    compileOnly(project(":arc-ollama-client"))

    compileOnly("io.micrometer:micrometer-registry-atlas:1.12.3")
    compileOnly("com.azure:azure-identity:1.13.1")

    implementation("org.springframework.boot:spring-boot-autoconfigure:3.3.3")
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.3.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.3")
    testImplementation("org.springframework.boot:spring-boot-starter:3.3.3")
}
