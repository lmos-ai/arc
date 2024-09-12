// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

dependencies {

    implementation(project(":arc-agents"))
    implementation(project(":arc-result"))
    implementation(project(":arc-api"))

    // Graphql
    implementation("com.expediagroup:graphql-kotlin-spring-server:7.1.4") {
        exclude(group = "com.graphql-java", module = "graphql-java")
    }
    implementation("com.graphql-java:graphql-java:21.5")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.3")

    // Test
    testImplementation(project(":arc-spring-boot-starter"))
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.3")
}
