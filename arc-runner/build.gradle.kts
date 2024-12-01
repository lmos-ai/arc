// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    implementation(project(":arc-agents"))
    implementation(project(":arc-result"))
    implementation(project(":arc-api"))
    implementation(project(":arc-azure-client"))
    implementation(project(":arc-spring-boot-starter"))
    implementation(project(":arc-graphql-spring-boot-starter"))
    implementation(project(":arc-reader-html"))

    implementation("com.graphql-java:graphql-java:21.5")
    implementation("com.azure:azure-identity:1.13.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.4.0")
    implementation("info.picocli:picocli:4.7.6")
}
