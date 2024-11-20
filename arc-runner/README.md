<!--
SPDX-FileCopyrightText: 2024 Deutsche Telekom AG

SPDX-License-Identifier: CC0-1.0    
-->
# Arc Runner (Experimental)

## Install Arc-Runner

1. Install Arc Runner
```
curl -Ls https://sh.jbang.dev | bash -s - app install --fresh --force https://github.com/lmos-ai/arc/blob/main/arc-runner/arc.java
```
(This will install everything you need to run the arc-runner, including JAVA)

2. Install the Weather Agent
```
arc install weather
```
(This will install the weather agent located at https://github.com/lmos-ai/arc/blob/main/arc-runner/weather.agent.kts)

3. Start Arc Runner 
```
export ARC_AI_URL=https://gpt4-se-dev.openai.azure.com/ // The url hosting the models. Can be omitted if using openai.
export ARC_AI_KEY=YOUR_OPENAI_KEY // The key to authenticate with the AI service. can be omitted if using Azure Login.
export ARC_CLIENT=openai // or azure, ollama, etc.
export ARC_MODEL=GPT-4o // the name of the model to use
arc run 
```

4. Chat with the weather agent
```
open browser at http://localhost:8080/chat/index.html
```
