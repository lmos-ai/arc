---
title: Azure OpenAI Client
---

>Azure OpenAI is a managed service that allows developers to deploy, 
>tune, and generate content from OpenAI models on Azure resources.
>
>The Azure OpenAI client library for Java is an adaptation of OpenAI's REST APIs that 
>provides an idiomatic interface and rich integration with the rest of the Azure SDK ecosystem."
>
> <cite> [github.com/Azure/azure-sdk-for-java](https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/openai/azure-ai-openai/README.md) </cite>
>
> 
See https://learn.microsoft.com/en-us/java/api/overview/azure/ai-openai-readme?view=azure-java-preview

Example:
```kotlin
val config = AzureClientConfig(
    modelName = "gpt-3.5-turbo",
    apiKey = "YOUR_API_KEY",
    url = "https://api.openai.com/v1/engines/gpt-3.5-turbo/completions"
)
val azureOpenAIClient = OpenAIClientBuilder()
    .endpoint(config.url)
    .credential(AzureKeyCredential(config.apiKey))
    .buildAsyncClient()

val agentClient = AzureAIClient(config, azureOpenAIClient)
```