package com.focussound.ai

import com.focussound.data.FocusMode

class RuleBasedSoundPromptParser {
    fun parse(prompt: String): SoundIntent {
        val lower = prompt.lowercase()
        val mode = when {
            lower.hasAny("coding", "code", "programming", "코딩", "개발") -> FocusMode.CODING
            lower.hasAny("reading", "read", "book", "독서", "책") -> FocusMode.READING
            lower.hasAny("sleep", "night", "수면", "잠", "밤") -> FocusMode.SLEEP
            lower.hasAny("study", "learn", "공부", "학습", "시험") -> FocusMode.STUDY
            else -> FocusMode.STUDY
        }

        val brightnessHint = when {
            lower.hasAny("bright", "clear", "sharp", "밝게", "선명", "또렷") -> 0.68f
            lower.hasAny("soft", "gentle", "dark", "부드럽", "자극", "고역 줄", "어둡") -> 0.28f
            else -> null
        }
        val warmthHint = when {
            lower.hasAny("warm", "deep", "low", "따뜻", "포근", "낮은", "저역") -> 0.74f
            lower.hasAny("cool", "clean", "dry", "차갑", "깔끔") -> 0.35f
            else -> null
        }
        val movementHint = when {
            lower.hasAny("moving", "flow", "alive", "움직", "흐름", "변화") -> 0.42f
            lower.hasAny("steady", "static", "stable", "반복", "고정", "안정") -> 0.16f
            else -> null
        }
        val textureHint = when {
            lower.hasAny("rain", "rainy", "비", "빗소리") -> TextureHint.RAINY
            lower.hasAny("library", "도서관") -> TextureHint.LIBRARY
            lower.hasAny("cyber", "terminal", "matrix", "사이버", "터미널") -> TextureHint.CYBER
            lower.hasAny("pad", "ambient", "warm pad", "패드", "앰비언트") -> TextureHint.WARM_PAD
            lower.hasAny("room", "deep room", "공간", "방") -> TextureHint.DEEP_ROOM
            lower.hasAny("sleep dark", "dark sleep", "깊은 수면") -> TextureHint.SLEEP_DARK
            lower.hasAny("night", "밤", "야간") -> TextureHint.NIGHT
            else -> TextureHint.CLEAN
        }
        val fatigueTarget = when {
            lower.hasAny("very low", "least", "아주 낮", "최대한 부드럽", "자극 적") -> FatigueTarget.VERY_LOW
            lower.hasAny("normal", "선명", "활기") -> FatigueTarget.NORMAL
            else -> FatigueTarget.LOW
        }

        return SoundIntent(
            mode = mode,
            moodKeywords = extractMoodKeywords(lower),
            fatigueTarget = fatigueTarget,
            brightnessHint = brightnessHint,
            warmthHint = warmthHint,
            movementHint = movementHint,
            textureHint = textureHint,
            durationMinutes = extractDuration(lower)
        )
    }

    private fun extractMoodKeywords(text: String): List<String> {
        return listOf("rain", "night", "warm", "deep", "library", "coding", "sleep", "비", "밤", "따뜻", "차분", "도서관", "코딩", "수면")
            .filter { text.contains(it) }
            .distinct()
    }

    private fun extractDuration(text: String): Int? {
        val match = Regex("(\\d+)\\s*(분|min|minutes?)").find(text)
        return match?.groupValues?.getOrNull(1)?.toIntOrNull()?.coerceIn(5, 180)
    }

    private fun String.hasAny(vararg needles: String): Boolean {
        return needles.any { contains(it) }
    }
}
