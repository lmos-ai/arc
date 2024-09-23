# Guardrails DSL for AI Agents

A comprehensive Kotlin-based Domain-Specific Language (DSL) for defining and applying guardrails to AI agents. This DSL allows developers to specify various filters and constraints to ensure the safety, reliability, and compliance of AI agent interactions.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
    - [Implemented Guardrails](#implemented-guardrails)
- [Usage](#usage)
    - [Defining Guardrails](#defining-guardrails)
    - [Applying Guardrails](#applying-guardrails)
- [Examples](#examples)
    - [Basic Usage](#basic-usage)
    - [Advanced Usage with Nesting](#advanced-usage-with-nesting)
- [Testing](#testing)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

## Introduction

This project provides a flexible and expressive DSL for defining guardrails that can be applied to AI agents. The guardrails ensure that the agents behave appropriately, comply with policies, and handle user inputs and outputs securely and responsibly.

The DSL is designed to be intuitive and natural for developers, allowing for complex guardrail configurations without modifying existing agent code.

## Features

### Implemented Guardrails

- **Profanity Filter**: Replaces specified profane words with given replacements.
- **Custom Filter**: Applies custom regex replacements to messages.
- **Length Filter**: Limits the message content to a specified maximum length.
- **Regex Filter**: Applies regex patterns with replacements.
- **LLM Filter**: Interacts with a Language Model (LLM) to process messages and apply nested guardrails based on the response.
- **API Filter**: Makes API calls and applies nested guardrails based on the API response.
- **Conditional Filter**: Applies filters conditionally based on message content or context variables.
- **Try-Catch Filter**: Handles errors within guardrails and allows for custom error handling.
- **Error Handlers**: Logs messages or performs actions when errors occur.
- **Blacklist Filter**: Blocks messages containing blacklisted terms read from a file.
- **Whitelist Validation Filter**: Validates message content against a whitelist read from a file.
- **Preprocessor Filter**: Replaces sensitive information (e.g., emails, URLs, phone numbers) with placeholders before processing.
- **Postprocessor Filter**: Restores original sensitive information after processing.

## Usage

### Defining Guardrails

To define guardrails, use the DSL within your agent definition. Here's how you can define guardrails for input and output filters:

```kotlin
import ai.ancf.lmos.arc.guardrail.*

val agent = AgentDefinition().apply {
    name = "SecureAgent"
    description = "An agent with comprehensive guardrails"
    model = { "gpt-3.5-turbo" }

    // Apply guardrails in input filter
    filterInput {
        applyGuardrails {
            // Profanity filter
            filter("profanity") {
                replace("badword", "***")
                replace("anotherbadword", "***")
            }

            // Custom regex filter
            filter("custom") {
                replace("\\d{4}-\\d{2}-\\d{2}", "[DATE]")
            }

            // Length filter
            length(500)

            // Preprocessing to replace sensitive information
            preprocess {
              patterns = listOf(
                Pair("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})", "EMAIL"),
                Pair("\\d{3}-\\d{2}-\\d{4}", "PHONE"),
              )
            }

            // Conditional guardrails
            `if`(Condition.Equals("help")) {
                length(100)
            } `else` {
                length(200)
            }

            // Try-catch guardrails
            tryCatch({
                length(1000)
            }, {
                log("Error applying guardrails")
            })

            // Nested guardrails with LLM
            llm {
                userMessage("Analyze the message for sensitive content.")
                guardrails {
                    filter("custom") {
                        replace("sensitive", "[REDACTED]")
                    }
                }
            }

            // Nested guardrails with API
            api("https://api.example.com/check") {
                query("content", "{message.content}")
                guardrails {
                    blacklist {
                        fromFile("path/to/blacklist.txt")
                    }
                }
            }
        }
    }

    // Apply guardrails in output filter
    filterOutput {
        applyGuardrails {
            // Postprocessing to restore sensitive information
            postprocess {}

            // Regex filter to redact secrets
            regex("secret", {
                replace("[REDACTED]")
            })
        }
    }
}
```

### Applying Guardrails

The guardrails are applied within the `filterInput` and `filterOutput` blocks of your agent definition. The `applyGuardrails` function takes a lambda where you define your guardrails using the DSL.

## Examples

### Basic Usage

#### Profanity Filter

```kotlin
applyGuardrails {
    filter("profanity") {
        replace("badword", "***")
        replace("anotherbadword", "***")
    }
}
```

#### Length Filter

```kotlin
applyGuardrails {
    length(500)
}
```

#### Custom Regex Filter

```kotlin
applyGuardrails {
    filter("custom") {
        replace("\\d{4}-\\d{2}-\\d{2}", "[DATE]")
    }
}
```

### Advanced Usage with Nesting

#### Conditional Guardrails

```kotlin
applyGuardrails {
    `if`(Condition.Equals("Please help me.")) {
        length(100)
    } elseBlock = {
        length(200)
    }
}
```

#### Nested Guardrails with LLM

```kotlin
applyGuardrails {
    llm {
        userMessage("Analyze the message for sensitive content.")
        guardrails {
            `if`(Condition.ContextEquals("llm_response_${message.turnId}", "Sensitive content detected.")) {
                filter("custom") {
                    replace("sensitive", "[REDACTED]")
                }
            }
        }
    }
}
```

#### Nested Guardrails with API

```kotlin
applyGuardrails {
    api("https://api.example.com/analyze") {
        query("content", "{message.content}")
        guardrails {
            `if`(Condition.ContextEquals("api_response_${message.turnId}", "ActionNeeded")) {
                length(100)
            }
        }
    }
}
```

## Testing

The project includes comprehensive test cases using the MockK library to mock dependencies and test all filters and DSL constructs. The tests cover:

- Individual filters (e.g., ProfanityFilter, CustomFilter, LengthFilter)
- Nested guardrails
- Conditional logic
- Error handling
- Preprocessing and postprocessing
- Interaction with LLMs and APIs

To run the tests, use the following command:

```bash
./gradlew test
```

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Implement your changes, ensuring that you follow the project's coding standards.
4. Write tests for your changes.
5. Submit a pull request with a detailed description of your changes.

Please make sure to discuss significant changes or new features via issues before starting work to ensure alignment with the project's goals.

---