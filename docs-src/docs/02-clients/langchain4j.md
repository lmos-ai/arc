---
title: LangChain4J
---

LangChain4J is a Java library that provides a uniform interface to multiple language models.

The Arc Framework provides a wrapper for the LangChain4J ChatLanguageModel interface, 
which allows us to use any LangChain4J client within our Arc Agents.

Supported LangChain4J clients:

| Model          | Package                                      | Supported Version |  
|----------------|----------------------------------------------|-------------------|
| Amazon Bedrock | dev.langchain4j:langchain4j-bedrock          | 0.36.2            | 
| Google Gemini  | dev.langchain4j:langchain4j-google-ai-gemini | 0.36.2            |   
| Google Gemini  | dev.langchain4j:langchain4j-ollama           | 0.36.2            |   


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

