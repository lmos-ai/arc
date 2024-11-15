---
title: Ollama Client
---

The Ollama Client connects to the Completion API of a running Ollama server.

Ollama is a great way to run LLM models locally. Even without expensive hardware, smaller models, 
such as Gemma 7B, can be a great way to start experimenting with Arc Agents.

See https://ollama.com/ for more details.

```kotlin
val client = OllamaClient(OllamaClientConfig("modelName", "url"), eventPublisher)
```