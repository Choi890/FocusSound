package com.focussound.ai

class PromptParserFallback(
    private val aiParser: SoundPromptParser?,
    private val ruleParser: RuleBasedSoundPromptParser
) : SoundPromptParser {
    override suspend fun parse(prompt: String): SoundIntent {
        return try {
            aiParser?.parse(prompt) ?: ruleParser.parse(prompt)
        } catch (_: Exception) {
            ruleParser.parse(prompt)
        }
    }
}
