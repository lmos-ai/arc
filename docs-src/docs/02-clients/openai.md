---
title: OpenAI Native Client
---

>OpenAI Native Client allows developers to interact directly with OpenAI's API with OpenAI's official java SDK.
>
>The OpenAI Native client library for Java provides a straightforward interface to OpenAI's REST APIs, enabling seamless integration with your applications.
>
> <cite> [github.com/OpenAI/openai-java](https://github.com/OpenAI/openai-java) </cite>
>
>
See https://github.com/OpenAI/openai-java

Example:
```kotlin
val config = OpenAINativeClientConfig(
    modelName = "gpt-4o",
    apiKey = "YOUR_API_KEY",
    url = "https://api.openai.com/v1/chat/completions"
)
val openAIClient = OpenAIOkHttpClientAsync.builder()
                .apiKey(config.apiKey)
                .build()

val agentClient = OpenAINativeClient(config, openAIClient)