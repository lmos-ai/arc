// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("ArcGradlePlugin") {
            id = "ai.ancf.lmos.arc.gradle.plugin"
            implementationClass = "ai.ancf.lmos.arc.gradle.plugin.ArcPlugin"
            version = version
        }
    }
}
