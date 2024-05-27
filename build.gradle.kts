// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.lang.System.getenv
import java.net.URI

plugins {
    kotlin("jvm") version "1.9.23" apply false
    kotlin("plugin.serialization") version "1.9.23" apply false
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.cyclonedx.bom") version "1.8.2" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
    id("org.gradle.crypto.checksum") version "1.4.0" apply false
}

subprojects {
    group = "io.github.lmos-ai.arc"
    version = "0.29.0"

    apply(plugin = "org.cyclonedx.bom")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.kotlinx.kover")
    apply(plugin = "org.gradle.crypto.checksum")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
        // withJavadocJar()
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(true)
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            freeCompilerArgs += "-Xcontext-receivers"
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
        dependsOn(tasks.dokkaJavadoc)
        from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
        archiveClassifier.set("javadoc")
    }

    configure<PublishingExtension> {
        publications {
            create("Maven", MavenPublication::class.java) {
                from(components["java"])
                artifact(javadocJar)
                pom {
                    description = "ARC is an AI framework."
                    url = "https://github.com/lmos-ai/arc"
                    scm {
                        url = "https://github.com/lmos-ai/arc.git"
                    }
                    licenses {
                        license {
                            name = "Apache-2.0"
                            distribution = "repo"
                            url = "https://github.com/lmos-ai/arc/blob/main/LICENSES/Apache-2.0.txt"
                        }
                    }
                    developers {
                        developer {
                            id = "pat"
                            name = "Patrick Whelan"
                            email = "opensource@telekom.de"
                        }
                        developer {
                            id = "bharat_bhushan"
                            name = "Bharat Bhushan"
                            email = "opensource@telekom.de"
                        }
                        developer {
                            id = "merrenfx"
                            name = "Max Erren"
                            email = "opensource@telekom.de"
                        }
                    }
                }
            }
        }

        repositories {
            maven {
                name = "github"
                url = URI("https://maven.pkg.github.com/lmos-ai/arc")
                credentials {
                    username = findProperty("GITHUB_USER")?.toString() ?: getenv("GITHUB_USER")
                    password = findProperty("GITHUB_TOKEN")?.toString() ?: getenv("GITHUB_TOKEN")
                }
            }
        }

        configure<SigningExtension> {
            sign(publications)
        }
    }

    dependencies {
        val kotlinXVersion = "1.8.0"
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinXVersion")
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinXVersion")
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinXVersion")
        "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

        // Testing
        "testImplementation"("org.junit.jupiter:junit-jupiter:5.10.2")
        "testImplementation"("org.assertj:assertj-core:3.25.3")
        "testImplementation"("io.mockk:mockk:1.13.10")
    }

    repositories {
        mavenCentral()
        google()
    }
}

dependencies {
    kover(project("arc-scripting"))
    kover(project("arc-azure-client"))
    kover(project("arc-ollama-client"))
    kover(project("arc-gemini-client"))
    kover(project("arc-result"))
    kover(project("arc-reader-pdf"))
    kover(project("arc-reader-html"))
    kover(project("arc-agents"))
    kover(project("arc-spring-boot-starter"))
    kover(project("arc-memory-mongo-spring-boot-starter"))
}

repositories {
    mavenCentral()
}

fun Project.java(configure: Action<JavaPluginExtension>): Unit =
    (this as ExtensionAware).extensions.configure("java", configure)
