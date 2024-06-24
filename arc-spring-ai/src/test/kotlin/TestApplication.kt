// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package io.github.lmos.arc.spring.ai

import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class TestApplication {

    @Bean
    open fun chatCompleterProvider() = SpringChatClient(
        OllamaChatModel(
            OllamaApi("http://localhost:8888"),
            OllamaOptions.create().withModel("llama3:8b"),
        ),
        "llama3:8b",
    )
}
