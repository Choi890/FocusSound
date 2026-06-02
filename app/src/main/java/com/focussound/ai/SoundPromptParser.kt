package com.focussound.ai

interface SoundPromptParser {
    suspend fun parse(prompt: String): SoundIntent
}
