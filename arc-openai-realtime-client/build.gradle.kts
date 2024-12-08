// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    implementation(project(":arc-result"))
    implementation(project(":arc-agents"))

    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.client.cio.jvm)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.core)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter)
}
