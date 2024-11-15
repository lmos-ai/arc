
# Integrating LangChain4j

The Arc Agent Framework works well with LangChain4j (https://docs.langchain4j.dev/).

When using the Arc Spring Boot Starter, you can easily configure LangChain4j clients to connect to your model.

Activate a Langchain4j client by adding the corresponding dependency to your gradle file:

| Model          | Package                                      | Supported Version |  
|----------------|----------------------------------------------|-------------------|
| Amazon Bedrock | dev.langchain4j:langchain4j-bedrock          | 0.35.0            | 
| Google Gemini  | dev.langchain4j:langchain4j-google-ai-gemini | 0.35.0            |   


Then configure the client in your Spring Boot application yaml:
```yaml
arc:
  ai:
    clients:
      - id: [BEDROCK_MODEL_ID] // used in the Arc Agent DSL in the model field.
        client: bedrock
        url: [AMAZON_REGION] // example, us-west-2
        accessKey: [YOUR_ACCESS_KEY]
        accessSecret: [YOUR_ACCESS_SECRET]
        modelName: [MODEL_NAME]
        
      - id: [GEMINI_MODEL_ID] // used in the Arc Agent DSL in the model field.
        client: gemini
        apiKey: [YOUR_API_KEY] // See https://ai.google.dev/gemini-api/docs/api-key
        modelName: [MODEL_NAME]
```