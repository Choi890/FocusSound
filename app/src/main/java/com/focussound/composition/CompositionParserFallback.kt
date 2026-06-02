package com.focussound.composition

class CompositionParserFallback(
    private val llmParser: CompositionPromptParser?,
    private val ruleParser: RuleBasedCompositionPromptParser
) : CompositionPromptParser {
    override suspend fun parse(prompt: String): CompositionIntent {
        return try {
            llmParser?.parse(prompt) ?: ruleParser.parse(prompt)
        } catch (_: Exception) {
            ruleParser.parse(prompt)
        }
    }
}
