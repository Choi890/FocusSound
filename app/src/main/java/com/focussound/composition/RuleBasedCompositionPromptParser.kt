package com.focussound.composition

import com.focussound.ai.FatigueTarget
import com.focussound.data.FocusMode

class RuleBasedCompositionPromptParser : CompositionPromptParser {
    override suspend fun parse(prompt: String): CompositionIntent {
        val text = prompt.lowercase()
        val mode = when {
            text.hasAny("coding", "code", "코딩", "개발", "새벽") -> FocusMode.CODING
            text.hasAny("sleep", "수면", "잠", "드론") -> FocusMode.SLEEP
            text.hasAny("reading", "독서", "책") -> FocusMode.READING
            else -> FocusMode.STUDY
        }
        val genre = when {
            text.hasAny("orchestra", "orchestral", "strings", "오케스트라", "현악") -> CompositionGenre.ORCHESTRAL_PAD
            text.hasAny("classic", "classical", "클래식", "미니멀") -> CompositionGenre.CLASSICAL_MINIMAL
            text.hasAny("sleep", "drone", "수면", "드론") -> CompositionGenre.SLEEP_DRONE
            text.hasAny("lofi", "lo-fi", "로파이") -> CompositionGenre.LOFI
            else -> CompositionGenre.AMBIENT_CODING
        }
        val fatigueTarget = when {
            text.hasAny("수면", "부드럽", "자극", "편안", "gentle", "soft") -> FatigueTarget.VERY_LOW
            text.hasAny("선명", "활기", "clear") -> FatigueTarget.NORMAL
            else -> FatigueTarget.LOW
        }
        val keyHint = when {
            text.contains("minor") || text.contains("마이너") -> MusicalKey(PitchClass.A, ScaleType.MINOR)
            text.contains("major") || text.contains("메이저") -> MusicalKey(PitchClass.C, ScaleType.MAJOR)
            text.hasAny("새벽", "night", "밤") -> MusicalKey(PitchClass.D, ScaleType.DORIAN)
            text.hasAny("수면", "sleep") -> MusicalKey(PitchClass.C, ScaleType.AMBIENT)
            else -> null
        }
        val tempo = extractTempo(text) ?: when (genre) {
            CompositionGenre.SLEEP_DRONE -> 56
            CompositionGenre.CLASSICAL_MINIMAL -> 72
            CompositionGenre.ORCHESTRAL_PAD -> 64
            CompositionGenre.LOFI -> 78
            CompositionGenre.AMBIENT_CODING -> 70
        }
        val melodyDensity = when {
            text.hasAny("멜로디 줄", "melody less", "less melody") -> 0.12f
            text.hasAny("멜로디", "선율") -> 0.42f
            genre == CompositionGenre.SLEEP_DRONE -> 0.05f
            else -> 0.24f
        }
        val rhythmDensity = when {
            text.hasAny("리듬 줄", "no rhythm", "리듬 없이") -> 0.05f
            text.hasAny("리듬", "beat", "비트") -> 0.36f
            genre == CompositionGenre.LOFI -> 0.32f
            else -> 0.12f
        }
        val harmonicComplexity = when {
            text.hasAny("복잡", "재즈", "풍부") -> 0.58f
            text.hasAny("단순", "minimal", "미니멀", "수면") -> 0.18f
            genre == CompositionGenre.ORCHESTRAL_PAD -> 0.42f
            else -> 0.3f
        }

        return CompositionIntent(
            mode = mode,
            genre = genre,
            moodKeywords = extractMoodKeywords(text),
            fatigueTarget = fatigueTarget,
            keyHint = keyHint,
            tempoHintBpm = tempo.coerceIn(45, 110),
            melodyDensityHint = melodyDensity,
            rhythmDensityHint = rhythmDensity,
            harmonicComplexityHint = harmonicComplexity,
            padFocus = text.hasAny("pad", "패드", "오케스트라", "드론", "ambient"),
            durationMinutes = extractDuration(text)
        )
    }

    private fun extractTempo(text: String): Int? {
        return Regex("(\\d+)\\s*(bpm|템포)").find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun extractDuration(text: String): Int? {
        return Regex("(\\d+)\\s*(분|min|minutes?)").find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()?.coerceIn(5, 180)
    }

    private fun extractMoodKeywords(text: String): List<String> {
        return listOf("새벽", "코딩", "클래식", "오케스트라", "수면", "패드", "로파이", "미니멀", "드론", "독서")
            .filter { text.contains(it) }
    }

    private fun String.hasAny(vararg values: String): Boolean = values.any { contains(it) }
}
