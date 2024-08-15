// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    api(project(":arc-result"))
    api(project(":arc-agents"))
    implementation(project(":arc-scripting"))

    compileOnly("io.micrometer:micrometer-registry-atlas:1.12.3")

    implementation("org.springframework.boot:spring-boot-autoconfigure:3.3.2")
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.3.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.2")
    testImplementation("org.springframework.boot:spring-boot-starter:3.3.2")
}
