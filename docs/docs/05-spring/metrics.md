---
title: Metrics
---

The Arc Framework Spring Boot Stater provides support
for metrics using the Micrometer library.

If an instance of `io.micrometer.core.instrument.MeterRegistry` is found on the classpath,
then the Arc Framework will automatically use it to public metrics.

A common way to use Micrometer is to add the `micrometer-registry-prometheus` dependency to your project.

```kts
implementation("io.micrometer:micrometer-registry-prometheus")
```

Currently, the following metrics are published:

| Name               | Description                                     | Type  | Tags             |
|--------------------|-------------------------------------------------|-------|------------------|
| arc.agent.finished | Records the time of a call to an Agent.         | Timer | `agent`, `model` |
| arc.agent.failed   | Records the number of failed calls to an Agent. | Timer | `agent`          |
| arc.llm.finished   | Records the time of a call to an LLM.           | Timer | `model`          |

