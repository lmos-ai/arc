// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.client.ollama

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

open class TestBase {

    companion object {

        lateinit var server: ApplicationEngine

        @BeforeAll
        @JvmStatic
        fun setup() {
            server = embeddedServer(Netty, port = 8080) {
                routing {
                    post("/api/chat") {
                        require(call.receiveText().contains(""""test question"""))
                        call.respondText(
                            Json.encodeToString(
                                ChatResponse(
                                    50,
                                    100,
                                    ChatMessage(role = "assistant", content = "answer to test"),
                                ),
                            ),
                        )
                    }
                    post("/api/embeddings") {
                        require(call.receiveText().contains(""""model":"llama3:8b""""))
                        call.respondText(""" {  "embedding": [0.0, 0.1] } """)
                    }
                }
            }.start()
        }

        @AfterAll
        @JvmStatic
        fun shutdown() {
            server.stop()
        }
    }
}
