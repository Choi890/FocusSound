package com.focussound.composition

interface CompositionPromptParser {
    suspend fun parse(prompt: String): CompositionIntent
}
