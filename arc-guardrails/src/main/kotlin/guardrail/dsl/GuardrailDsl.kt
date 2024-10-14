// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.dsl

import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings
import ai.ancf.lmos.arc.guardrail.extensions.FilterGroup
import ai.ancf.lmos.arc.guardrail.filters.*
import java.io.File
import java.io.IOException

/**
 * The [GuardrailDslMarker] annotation is used to mark the DSL classes and functions which needs to be excluded by ktlint.
 */
annotation class GuardrailDslMarker

@DslMarker
annotation class GuardrailDsl

/**
 * The [GuardrailBuilder] class is used to build a list of [AgentFilter]s using a DSL.
 */
@GuardrailDsl
class GuardrailBuilder(private val context: DSLContext) {
    private val filters = mutableListOf<AgentFilter>()

    /**
     *  Add a profanity or custom filter to the list of filters.
     */
    fun filter(type: String, block: FilterBodyBuilder.() -> Unit) {
        val builder = FilterBodyBuilder().apply(block)
        when (type.lowercase()) {
            "profanity" -> filters.add(builder.buildProfanityFilter())
            "custom" -> filters.add(builder.buildCustomFilter())
            else -> throw IllegalArgumentException("Unknown filter type: $type")
        }
    }

    /**
     * Top level function to define guardrails.
     */
    fun guardrails(block: GuardrailBuilder.() -> Unit) {
        val nestedBuilder = GuardrailBuilder(context).apply(block)
        filters.add(FilterGroup(nestedBuilder.build()))
    }

    /**
     * Add a context validation filter to the list of filters.
     */
    fun validateContext(contextVariableName: String) {
        filters.add(ContextValidationFilter(context, contextVariableName))
    }

    /**
     * Add a length filter to the list of filters.
     */
    fun length(maxLength: Int) {
        filters.add(LengthFilter(maxLength))
    }

    /**
     * Adds a regex replacement filter to the list of filters.
     */
    fun regexReplacement(pattern: String, block: RegexReplacementBodyBuilder.() -> Unit) {
        val builder = RegexReplacementBodyBuilder().apply(block)
        filters.add(RegexReplacementFilter(pattern, builder.replacement))
    }

    /**
     * Adds a LLM filter to the list of filters.
     */
    fun llm(block: LlmNestedBuilder.() -> Unit) {
        val builder = LlmNestedBuilder(context).apply(block)
        filters.add(builder.build())
    }

    /**
     * Adds an API filter to the list of filters.
     */
    fun api(url: String, block: ApiNestedBuilder.() -> Unit) {
        val builder = ApiNestedBuilder(url, context).apply(block)
        filters.add(builder.build())
    }

    /**
     * Adds a conditional filter to the list of filters.
     */
    @GuardrailDslMarker
    fun `if`(condition: Condition, block: IfBlockBuilder.() -> Unit) {
        val builder = IfBlockBuilder(condition, context).apply(block)
        filters.add(builder.build())
    }

    /**
     * Adds a try catch filter to the list of filters.
     */
    fun tryCatch(block: GuardrailBuilder.() -> Unit, catchBlock: ErrorHandlingBuilder.() -> Unit) {
        val tryFilters = GuardrailBuilder(context).apply(block).build()
        val errorHandlers = ErrorHandlingBuilder().apply(catchBlock).build()
        filters.add(TryCatchFilter(tryFilters, errorHandlers, context))
    }

    /**
     * Adds a preprocessor filter to the list of filters. This filter in intended to be used for context preprocessing before LLM calls.
     */
    fun contextpreprocess(contextVariableName: String, block: ReplacementPrePostBuilder.() -> Unit) {
        val builder = ReplacementPrePostBuilder(context).apply(block)
        filters.add(builder.buildContextReplacementPreprocessor(contextVariableName))
    }

    /**
     * Adds a preprocessor filter to the list of filters.
     */
    fun preprocess(block: ReplacementPrePostBuilder.() -> Unit) {
        val builder = ReplacementPrePostBuilder(context).apply(block)
        filters.add(builder.buildReplacementPreprocessor())
    }

    /**
     * Adds a postprocessor filter to the list of filters.
     */
    fun postprocess(block: ReplacementPrePostBuilder.() -> Unit) {
        val builder = ReplacementPrePostBuilder(context).apply(block)
        filters.add(builder.buildReplacementPostprocessor())
    }

    /**
     * build function to return the list of filters.
     */
    internal fun build(): List<AgentFilter> = filters
}

/**
 * The [ReplacementPrePostBuilder] class is used to build a [AgentFilter] for replacement preprocessing and postprocessing using a DSL.
 */
class ReplacementPrePostBuilder(private val context: DSLContext) {

    var patterns = listOf<Pair<String, String>>()

    fun buildContextReplacementPreprocessor(contextVariableName: String): AgentFilter {
        return CustomReplacementContextPreFilter(contextVariableName, context, patterns)
    }

    fun buildReplacementPreprocessor(): AgentFilter {
        return CustomReplacementPreFilter(context, patterns)
    }

    fun buildReplacementPostprocessor(): AgentFilter {
        return CustomReplacementPostFilter(context)
    }
}

/**
 * The [FilterBodyBuilder] class is used to build a [AgentFilter] for profanity and custom filters using a DSL.
 */
class FilterBodyBuilder {
    private val replacements = mutableListOf<Pair<String, String>>()

    fun replace(original: String, replacement: String) {
        replacements.add(original to replacement)
    }

    fun buildProfanityFilter(): AgentFilter {
        return ProfanityFilter(replacements.toMap())
    }

    fun buildCustomFilter(): AgentFilter {
        return CustomFilter(replacements.toMap())
    }
}

/**
 * The [RegexReplacementBodyBuilder] class is used to build a [AgentFilter] for regex replacement using a DSL.
 */
class RegexReplacementBodyBuilder {
    lateinit var replacement: String

    fun replace(replacement: String) {
        this.replacement = replacement
    }
}

/**
 * The [LlmBodyBuilder] class is used to build a [AgentFilter] utilizing a LLM call using a DSL.
 */
class LlmBodyBuilder {
    var userMessage: String? = null
    var systemMessage: String? = null
    var settings: ChatCompletionSettings? = null
    var model: String? = null
    private val examples = mutableListOf<Example>()

    fun userMessage(message: String) {
        userMessage = message
    }

    fun systemMessage(message: String) {
        systemMessage = message
    }

    fun settings(chatCompletionSettings: ChatCompletionSettings) {
        settings = chatCompletionSettings
    }

    fun model(llmModel: String) {
        model = llmModel
    }

    fun examples(block: ExamplesBuilder.() -> Unit) {
        val builder = ExamplesBuilder().apply(block)
        examples.addAll(builder.build())
    }

    fun getExamples(): List<Example> = examples
}

/**
 * The [ExamplesBuilder] class is used to build a list of [Example]s using a DSL.
 */
class ExamplesBuilder {
    private val examples = mutableListOf<Example>()

    fun example(block: ExampleBuilder.() -> Unit) {
        val builder = ExampleBuilder().apply(block)
        examples.add(builder.build())
    }

    fun build(): List<Example> = examples
}

/**
 * The [ExampleBuilder] class is used to build a [Example] using a DSL.
 */
class ExampleBuilder {
    var input: String? = null
    var output: String? = null

    fun input(input: String) {
        this.input = input
    }

    fun output(output: String) {
        this.output = output
    }

    fun build(): Example {
        return Example(input ?: "", output ?: "")
    }
}

/**
 * The [ApiBodyBuilder] class is used to build an API filter using a DSL.
 */
class ApiBodyBuilder {
    val params = mutableListOf<ApiParam>()

    fun query(name: String, value: String) {
        params.add(ApiParam.Query(name, value))
    }
}

/**
 * The [ErrorHandlingBuilder] class is used to build a list of [ErrorHandler]s using a DSL.
 */
class ErrorHandlingBuilder {
    private val handlers = mutableListOf<ErrorHandler>()

    fun log(message: String) {
        handlers.add(ErrorHandler.Log(message))
    }

    fun build(): List<ErrorHandler> = handlers
}

/**
 * The [BlacklistFilterBuilder] class is used to build a blacklist filter using a DSL.
 */
class BlacklistFilterBuilder(
    private val context: DSLContext,
) {
    private val blacklist = mutableSetOf<String>()
    private var filePath: String? = null

    fun block(term: String) {
        blacklist.add(term)
    }

    fun fromFile(path: String) {
        filePath = path
    }

    suspend fun fromContext(contextVariableName: String) {
        val terms = context.memory(contextVariableName) as List<String>
        blacklist.addAll(terms)
    }

    fun build(): AgentFilter {
        if (filePath != null) {
            val fileTerms = readLinesFromFile(filePath!!)
            blacklist.addAll(fileTerms)
        }
        return BlacklistFilter(context, blacklist.toList())
    }
}

/**
 * The [WhitelistValidationBuilder] class is used to build a whitelist validation filter using a DSL.
 * The whitelist can be provided as a list of values, from a file, or from a context variable.
 * It will block any matching values that are not in the whitelist for the given pattern.
 */
class WhitelistValidationBuilder(
    private val context: DSLContext,
) {
    private var disallowedPattern: String? = null
    private val whitelist = mutableSetOf<String>()
    private var filePath: String? = null
    private var errorMessage: String = "Unknown value found that is not in the whitelist."

    fun values(values: List<String>) {
        whitelist.addAll(values)
    }

    fun fromFile(path: String) {
        filePath = path
    }

    suspend fun fromContext(contextVariableName: String) {
        val terms = context.memory(contextVariableName) as List<String>
        whitelist.addAll(terms)
    }

    fun error(message: String) {
        errorMessage = message
    }

    fun build(): AgentFilter {
        if (filePath != null) {
            val fileTerms = readLinesFromFile(filePath!!)
            whitelist.addAll(fileTerms)
        }
        return WhitelistValidationFilter(context, disallowedPattern, whitelist.toList(), errorMessage)
    }
}

/**
 * The [LlmNestedBuilder] class is used to build a nested LLM filter using a DSL.
 */
class LlmNestedBuilder(
    private val context: DSLContext,
) {
    private val nestedFilters = mutableListOf<AgentFilter>()
    private val body = LlmBodyBuilder()

    fun userMessage(message: String) {
        body.userMessage = message
    }

    fun systemMessage(message: String) {
        body.systemMessage = message
    }

    fun examples(block: ExamplesBuilder.() -> Unit) {
        body.examples(block)
    }

    fun guardrails(block: GuardrailBuilder.() -> Unit) {
        val builder = GuardrailBuilder(context).apply(block)
        nestedFilters.addAll(builder.build())
    }

    fun build(): AgentFilter {
        return LlmNestedFilter(body, nestedFilters, context)
    }
}

/**
 * The [IfBlockBuilder] class is used to build a conditional filter using a DSL.
 */
class IfBlockBuilder(private val condition: Condition, private val context: DSLContext) {
    private val trueFilters = mutableListOf<AgentFilter>()
    private val falseFilters = mutableListOf<AgentFilter>()

    fun then(block: GuardrailBuilder.() -> Unit) {
        val builder = GuardrailBuilder(context).apply(block)
        trueFilters.addAll(builder.build())
    }

    @GuardrailDslMarker
    fun `else`(block: GuardrailBuilder.() -> Unit) {
        val builder = GuardrailBuilder(context).apply(block)
        falseFilters.addAll(builder.build())
    }

    internal fun build(): AgentFilter {
        return ConditionalFilter(condition, trueFilters, falseFilters, context)
    }
}

/**
 * The [ApiNestedBuilder] class is used to build a nested API filter using a DSL.
 */
class ApiNestedBuilder(
    private val url: String,
    private val context: DSLContext,
) {
    private val nestedFilters = mutableListOf<AgentFilter>()
    private val body = ApiBodyBuilder()

    fun query(name: String, value: String) {
        body.query(name, value)
    }

    fun guardrails(block: GuardrailBuilder.() -> Unit) {
        val builder = GuardrailBuilder(context).apply(block)
        nestedFilters.addAll(builder.build())
    }

    fun build(): AgentFilter {
        return ApiNestedFilter(url, body, nestedFilters, context)
    }
}

/**
 * Helper function to read lines from a file.
 */
private fun readLinesFromFile(path: String): List<String> {
    return try {
        File(path).readLines().map { it.trim() }.filter { it.isNotEmpty() }
    } catch (e: IOException) {
        throw RuntimeException("Failed to read file at $path", e)
    }
}
