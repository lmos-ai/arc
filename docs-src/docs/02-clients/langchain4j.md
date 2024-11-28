---
title: LangChain4J
---

LangChain4J is a Java library that provides a uniform interface to multiple language models.

The Arc Framework provides a wrapper for the LangChain4J ChatLanguageModel interface, 
which allows us to use any LangChain4J client within our Arc Agents.

Example:
```kotlin
// Gemini
val geminiClient = LangChainClient(
    LangChainConfig(
        modelName = config.modelName,
        url = config.url,
        accessKeyId = null,
        secretAccessKey = null,
        apiKey = config.apiKey,
    ),
    geminiBuilder(),
    eventPublisher,
  )

// Amazon Bedrock
val bedrockClient = LangChainClient(
    LangChainConfig(
        modelName = config.modelName,
        url = config.url,
        accessKeyId = config.accessKey,
        secretAccessKey = config.accessSecret,
        apiKey = null,
    ),
    bedrockBuilder(),
    eventPublisher,
)

// Ollama 
val ollamaClient = LangChainClient(
    LangChainConfig(
        modelName = config.modelName,
        url = config.url, // defaults to "http://localhost:11434"
    ),
    ollamaBuilder(),
    eventPublisher,
)

// The clients can then be used in a ChatCompleterProvider
val chatCompleterProvider = ChatCompleterProvider { clientId ->
    when (clientId) {
        "gemini" -> geminiClient
        "bedrock" -> bedrockClient
        "ollama" -> ollamaClient
        else -> throw IllegalArgumentException("Unknown client id: $clientId")
    }
}

```

