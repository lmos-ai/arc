// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.dsl

import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
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

    fun filter(type: String, block: FilterBodyBuilder.() -> Unit) {
        val builder = FilterBodyBuilder().apply(block)
        when (type.lowercase()) {
            "profanity" -> filters.add(builder.buildProfanityFilter())
            "custom" -> filters.add(builder.buildCustomFilter())
            else -> throw IllegalArgumentException("Unknown filter type: $type")
        }
    }

    fun guardrails(block: GuardrailBuilder.() -> Unit) {
        val nestedBuilder = GuardrailBuilder(context).apply(block)
        filters.add(FilterGroup(nestedBuilder.build()))
    }

    fun length(maxLength: Int) {
        filters.add(LengthFilter(maxLength))
    }

    fun regex(pattern: String, block: RegexBodyBuilder.() -> Unit) {
        val builder = RegexBodyBuilder().apply(block)
        filters.add(RegexFilter(pattern, builder.replacement))
    }

    fun llm(block: LlmNestedBuilder.() -> Unit) {
        val builder = LlmNestedBuilder(context).apply(block)
        filters.add(builder.build())
    }

    fun api(url: String, block: ApiNestedBuilder.() -> Unit) {
        val builder = ApiNestedBuilder(url, context).apply(block)
        filters.add(builder.build())
    }

    @GuardrailDslMarker
    fun `if`(condition: Condition, block: IfBlockBuilder.() -> Unit) {
        val builder = IfBlockBuilder(condition, context).apply(block)
        filters.add(builder.build())
    }

    fun tryCatch(block: GuardrailBuilder.() -> Unit, catchBlock: ErrorHandlingBuilder.() -> Unit) {
        val tryFilters = GuardrailBuilder(context).apply(block).build()
        val errorHandlers = ErrorHandlingBuilder().apply(catchBlock).build()
        filters.add(TryCatchFilter(tryFilters, errorHandlers, context))
    }

    fun contextpreprocess(block: ReplacementPrePostBuilder.() -> Unit) {
        val builder = ReplacementPrePostBuilder(context).apply(block)
        filters.add(builder.buildContextReplacementPreprocessor())
    }

    fun preprocess(block: ReplacementPrePostBuilder.() -> Unit) {
        val builder = ReplacementPrePostBuilder(context).apply(block)
        filters.add(builder.buildReplacementPreprocessor())
    }

    fun postprocess(block: ReplacementPrePostBuilder.() -> Unit) {
        val builder = ReplacementPrePostBuilder(context).apply(block)
        filters.add(builder.buildReplacementPostprocessor())
    }

    internal fun build(): List<AgentFilter> = filters
}

class ReplacementPrePostBuilder(private val context: DSLContext) {

    var patterns = listOf<Pair<String, String>>()

    fun buildContextReplacementPreprocessor(): AgentFilter {
        return CustomReplacementContextPreFilter(context, patterns)
    }

    fun buildReplacementPreprocessor(): AgentFilter {
        return CustomReplacementPreFilter(context, patterns)
    }

    fun buildReplacementPostprocessor(): AgentFilter {
        return CustomReplacementPostFilter(context)
    }
}

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

class RegexBodyBuilder {
    lateinit var replacement: String

    fun replace(replacement: String) {
        this.replacement = replacement
    }
}

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

class ExamplesBuilder {
    private val examples = mutableListOf<Example>()

    fun example(block: ExampleBuilder.() -> Unit) {
        val builder = ExampleBuilder().apply(block)
        examples.add(builder.build())
    }

    fun build(): List<Example> = examples
}

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

class ApiBodyBuilder {
    val params = mutableListOf<ApiParam>()

    fun query(name: String, value: String) {
        params.add(ApiParam.Query(name, value))
    }
}

class ErrorHandlingBuilder {
    private val handlers = mutableListOf<ErrorHandler>()

    fun log(message: String) {
        handlers.add(ErrorHandler.Log(message))
    }

    fun build(): List<ErrorHandler> = handlers
}

class BlacklistFilterBuilder {
    private val blacklist = mutableSetOf<String>()
    private var filePath: String? = null

    fun block(term: String) {
        blacklist.add(term)
    }

    fun fromFile(path: String) {
        filePath = path
    }

    fun build(): AgentFilter {
        if (filePath != null) {
            val fileTerms = readLinesFromFile(filePath!!)
            blacklist.addAll(fileTerms)
        }
        return BlacklistFilter(blacklist.toList())
    }
}

class WhitelistValidationBuilder {
    private val whitelist = mutableSetOf<String>()
    private var filePath: String? = null
    private var errorMessage: String = "Unknown value found that is not in the whitelist."

    fun values(values: List<String>) {
        whitelist.addAll(values)
    }

    fun fromFile(path: String) {
        filePath = path
    }

    fun error(message: String) {
        errorMessage = message
    }

    fun build(): AgentFilter {
        if (filePath != null) {
            val fileTerms = readLinesFromFile(filePath!!)
            whitelist.addAll(fileTerms)
        }
        return WhitelistValidationFilter(whitelist.toList(), errorMessage)
    }
}

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

// Helper function to read lines from a file
private fun readLinesFromFile(path: String): List<String> {
    return try {
        File(path).readLines().map { it.trim() }.filter { it.isNotEmpty() }
    } catch (e: IOException) {
        throw RuntimeException("Failed to read file at $path", e)
    }
}
