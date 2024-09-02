// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

import org.gradle.crypto.checksum.Checksum
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.System.getenv
import java.net.URI

plugins {
    kotlin("jvm") version "2.0.10" apply false
    kotlin("plugin.serialization") version "2.0.10" apply false
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.cyclonedx.bom") version "1.8.2" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
    id("org.gradle.crypto.checksum") version "1.4.0" apply false
}

subprojects {
    group = "io.github.lmos-ai.arc"

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
                    if (project.isBOM()) packaging = "pom"
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
                name = "GitHubPackages"
                url = URI("https://maven.pkg.github.com/lmos-ai/arc")
                credentials {
                    username = findProperty("GITHUB_USER")?.toString() ?: getenv("GITHUB_USER")
                    password = findProperty("GITHUB_TOKEN")?.toString() ?: getenv("GITHUB_TOKEN")
                }
            }
        }

        configure<SigningExtension> {
            useInMemoryPgpKeys(
                findProperty("signing.keyId") as String?,
                System.getenv("PGP_SECRET_KEY"),
                System.getenv("PGP_PASSPHRASE")
            )
            sign(publications)
        }
    }

    if (!project.name.endsWith("-bom")) {
        dependencies {
            val kotlinXVersion = "1.8.1"
            "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinXVersion")
            "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinXVersion")
            "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinXVersion")
            "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

            // Testing
            "testImplementation"("org.junit.jupiter:junit-jupiter:5.10.2")
            "testImplementation"("org.assertj:assertj-core:3.25.3")
            "testImplementation"("io.mockk:mockk:1.13.10")
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    tasks.named("dokkaJavadoc") {
        mustRunAfter("checksum")
    }

    tasks.register("copyPom") {
        doLast {
            println("${findProperty("LOCAL_MAVEN_REPO")}/io/github/lmos-ai/arc/${project.name}/${project.version}")
            val pomFolder =
                File("${findProperty("LOCAL_MAVEN_REPO")}/io/github/lmos-ai/arc/${project.name}/${project.version}")
            pomFolder.listFiles()?.forEach { file ->
                if (file.name.endsWith(".pom") || file.name.endsWith(".pom.asc")) {
                    file.copyTo(
                        File(project.layout.buildDirectory.dir("libs").get().asFile, file.name),
                        overwrite = true,
                    )
                }
            }
        }
    }

    tasks.register("cleanChecksum") {
        dependsOn("copyPom")
        doFirst {
            layout.buildDirectory.dir("libs").get().asFile.walk().forEach { file ->
                if (file.name.endsWith(".sha1") || file.name.endsWith(".md5")) {
                    println("Deleting ${file.name} ${file.delete()}")
                }
            }
        }
    }

    tasks.register<Checksum>("checksum") {
        dependsOn("cleanChecksum")
        inputFiles.setFrom(project.layout.buildDirectory.dir("libs"))
        outputDirectory.set(project.layout.buildDirectory.dir("libs"))
        checksumAlgorithm.set(Checksum.Algorithm.MD5)
    }

    tasks.register("sha1") {
        dependsOn("checksum")
        doLast {
            project.layout.buildDirectory.dir("libs").get().asFile.listFiles()?.forEach { file ->
                if (!file.name.endsWith(".md5")) {
                    "shasum ${file.name}".execWithCode(workingDir = file.parentFile).second.forEach {
                        File(file.parentFile, "${file.name}.sha1").writeText(it.substringBefore(" "))
                    }
                }
            }
        }
    }

    tasks.register("setupFolders") {
        dependsOn("sha1")
        doLast {
            val build = File(
                project.layout.buildDirectory.dir("out").get().asFile,
                "io/github/lmos-ai/arc/${project.name}/${project.version}",
            )
            build.mkdirs()
            project.layout.buildDirectory.dir("libs").get().asFile.listFiles()?.forEach { file ->
                file.copyTo(File(build, file.name), overwrite = true)
            }
        }
    }

    tasks.register<Zip>("packageSonatype") {
        doFirst {
            if (project.isBOM()) {
                println("Packaging BOM")
                project.layout.buildDirectory.dir("out").get().asFile.walk().forEach { file ->
                    if (file.isFile && !file.name.contains(".pom")) {
                        println("Deleting ${file.name}")
                        file.delete()
                    }
                }
            }
        }
        dependsOn("setupFolders")
        archiveFileName.set("${project.name}.zip")
        destinationDirectory.set(parent!!.layout.buildDirectory.dir("dist"))
        from(layout.buildDirectory.dir("out"))
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
    kover(project("arc-spring-ai"))
    kover(project("arc-api"))
    kover(project("arc-graphql-spring-boot-starter"))
    kover(project("arc-agent-client"))
}

repositories {
    mavenCentral()
}

fun Project.java(configure: Action<JavaPluginExtension>): Unit =
    (this as ExtensionAware).extensions.configure("java", configure)

fun String.execWithCode(workingDir: File? = null): Pair<CommandResult, Sequence<String>> {
    ProcessBuilder().apply {
        workingDir?.let { directory(it) }
        command(split(" "))
        redirectErrorStream(true)
        val process = start()
        val result = process.readStream()
        val code = process.waitFor()
        return CommandResult(code) to result
    }
}

class CommandResult(val code: Int) {

    val isFailed = code != 0
    val isSuccess = !isFailed

    fun ifFailed(block: () -> Unit) {
        if (isFailed) block()
    }
}

/**
 * Executes a string as a command.
 */
fun String.exec(workingDir: File? = null) = execWithCode(workingDir).second

fun Project.isBOM() = name.endsWith("-bom")

private fun Process.readStream() = sequence<String> {
    val reader = BufferedReader(InputStreamReader(inputStream))
    try {
        var line: String?
        while (true) {
            line = reader.readLine()
            if (line == null) {
                break
            }
            yield(line)
        }
    } finally {
        reader.close()
    }
}
