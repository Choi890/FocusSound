package com.focussound.ai

import com.focussound.ai.PromptKeywordDictionary.hasAny
import com.focussound.composition.CompositionGenre
import com.focussound.composition.CompositionIntent
import com.focussound.composition.MusicalKey
import com.focussound.composition.PitchClass
import com.focussound.composition.ScaleType
import com.focussound.data.FocusMode

class LocalCompositionPromptParser {
    fun parse(prompt: String): CompositionIntent {
        // Convert free-form prompt text into bounded musical controls for the local composer.
        val text = prompt.trim().ifBlank { "새벽 코딩용, 따뜻한 패드, 멜로디 적게" }
        val normalized = text.lowercase()
        val mode = extractMode(normalized)
        val genre = extractGenre(normalized, mode)
        val melodyDensity = extractMelodyDensity(normalized, mode, genre)
        val rhythmDensity = extractRhythmDensity(normalized, mode, genre)
        val padAmount = extractPadAmount(normalized, mode, genre)

        return CompositionIntent(
            mode = mode,
            genre = genre,
            moodKeywords = extractMood(normalized),
            fatigueTarget = extractFatigueTarget(normalized, mode),
            keyHint = extractKey(normalized, genre, mode),
            tempoHintBpm = extractTempo(normalized) ?: defaultTempo(mode, genre),
            melodyDensityHint = melodyDensity,
            rhythmDensityHint = rhythmDensity,
            harmonicComplexityHint = extractHarmonicComplexity(normalized, genre),
            padFocus = padAmount > 0.55f || normalized.contains("패드"),
            padAmountHint = padAmount,
            durationMinutes = extractDuration(normalized)
        )
    }

    private fun extractMode(text: String): FocusMode = when {
        text.hasAny(PromptKeywordDictionary.sleep) -> FocusMode.SLEEP
        text.hasAny(PromptKeywordDictionary.coding) -> FocusMode.CODING
        text.hasAny(PromptKeywordDictionary.reading) -> FocusMode.READING
        else -> FocusMode.STUDY
    }

    private fun extractGenre(text: String, mode: FocusMode): CompositionGenre = when {
        text.hasAny(PromptKeywordDictionary.lofi) -> CompositionGenre.LOFI
        text.hasAny(PromptKeywordDictionary.classical) -> CompositionGenre.CLASSICAL_MINIMAL
        text.hasAny(PromptKeywordDictionary.orchestral) -> CompositionGenre.ORCHESTRAL_PAD
        mode == FocusMode.SLEEP || text.contains("드론") -> CompositionGenre.SLEEP_DRONE
        else -> CompositionGenre.AMBIENT_CODING
    }

    private fun extractFatigueTarget(text: String, mode: FocusMode): FatigueTarget = when {
        mode == FocusMode.SLEEP -> FatigueTarget.VERY_LOW
        text.hasAny(PromptKeywordDictionary.lowFatigue) || text.hasAny(PromptKeywordDictionary.warm) -> FatigueTarget.VERY_LOW
        text.hasAny(PromptKeywordDictionary.bright) -> FatigueTarget.NORMAL
        else -> FatigueTarget.LOW
    }

    private fun extractTempo(text: String): Int? {
        // Keep detected BPM inside the calm-focus range used by the synthesis engine.
        return Regex("(\\d{2,3})\\s*(bpm|템포)?").find(text)
            ?.groupValues
            ?.getOrNull(1)
            ?.toIntOrNull()
            ?.coerceIn(45, 112)
    }

    private fun defaultTempo(mode: FocusMode, genre: CompositionGenre): Int = when {
        mode == FocusMode.SLEEP -> 54
        genre == CompositionGenre.LOFI -> 76
        genre == CompositionGenre.CLASSICAL_MINIMAL -> 70
        genre == CompositionGenre.ORCHESTRAL_PAD -> 62
        else -> 68
    }

    private fun extractMelodyDensity(text: String, mode: FocusMode, genre: CompositionGenre): Float = when {
        mode == FocusMode.SLEEP || genre == CompositionGenre.SLEEP_DRONE -> 0.04f
        text.contains("멜로디 없") || text.contains("멜로디 제거") -> 0.02f
        text.contains("멜로디 적") || text.contains("멜로디 줄") -> 0.12f
        text.contains("멜로디 많") || text.contains("선율") -> 0.38f
        genre == CompositionGenre.CLASSICAL_MINIMAL -> 0.28f
        else -> 0.2f
    }

    private fun extractRhythmDensity(text: String, mode: FocusMode, genre: CompositionGenre): Float = when {
        mode == FocusMode.SLEEP || mode == FocusMode.READING -> 0.02f
        text.contains("리듬 없") || text.contains("비트 없") -> 0.02f
        text.contains("리듬 줄") || text.contains("약한 리듬") -> 0.08f
        genre == CompositionGenre.LOFI || mode == FocusMode.CODING -> 0.16f
        else -> 0.05f
    }

    private fun extractPadAmount(text: String, mode: FocusMode, genre: CompositionGenre): Float = when {
        text.contains("패드 늘") || text.contains("패드 중심") -> 0.86f
        text.contains("패드 적") -> 0.35f
        mode == FocusMode.SLEEP || genre == CompositionGenre.ORCHESTRAL_PAD -> 0.78f
        else -> 0.58f
    }

    private fun extractHarmonicComplexity(text: String, genre: CompositionGenre): Float = when {
        text.contains("복잡") || text.contains("재즈") || text.contains("화성") -> 0.5f
        text.contains("단순") || text.contains("미니멀") -> 0.18f
        genre == CompositionGenre.ORCHESTRAL_PAD -> 0.42f
        else -> 0.28f
    }

    private fun extractKey(text: String, genre: CompositionGenre, mode: FocusMode): MusicalKey? = when {
        text.contains("minor") || text.contains("마이너") -> MusicalKey(PitchClass.A, ScaleType.MINOR)
        text.contains("major") || text.contains("메이저") -> MusicalKey(PitchClass.C, ScaleType.MAJOR)
        mode == FocusMode.SLEEP -> MusicalKey(PitchClass.C, ScaleType.AMBIENT)
        genre == CompositionGenre.LOFI -> MusicalKey(PitchClass.A, ScaleType.MINOR)
        else -> null
    }

    private fun extractDuration(text: String): Int? {
        return Regex("(\\d{1,3})\\s*(분|min|minutes?)").find(text)
            ?.groupValues
            ?.getOrNull(1)
            ?.toIntOrNull()
            ?.coerceIn(5, 180)
    }

    private fun extractMood(text: String): List<String> {
        // Mood tags are lightweight hints; the composer still falls back to a focus-oriented default.
        return buildList {
            if (text.hasAny(PromptKeywordDictionary.warm)) add("따뜻함")
            if (text.hasAny(PromptKeywordDictionary.dark)) add("어두움")
            if (text.hasAny(PromptKeywordDictionary.bright)) add("선명함")
            if (text.hasAny(PromptKeywordDictionary.ambient)) add("앰비언트")
            if (text.contains("새벽")) add("새벽")
            if (text.contains("차분")) add("차분함")
        }.ifEmpty { listOf("집중") }
    }
}
