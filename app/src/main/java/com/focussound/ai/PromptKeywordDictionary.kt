package com.focussound.ai

object PromptKeywordDictionary {
    val coding = listOf("코딩", "개발", "프로그래밍", "새벽 코딩", "coding", "code")
    val study = listOf("공부", "학습", "집중", "study")
    val reading = listOf("독서", "읽기", "책", "reading")
    val sleep = listOf("수면", "잠", "잠들기", "잘 때", "sleep", "drone")

    val lofi = listOf("로파이", "lofi", "lo-fi")
    val classical = listOf("클래식", "미니멀", "피아노", "classical", "minimal")
    val orchestral = listOf("오케스트라", "현악", "스트링", "패드 중심", "orchestra", "strings")
    val ambient = listOf("앰비언트", "ambient", "공간감", "새벽")

    val warm = listOf("따뜻", "부드럽", "포근", "warm", "soft")
    val dark = listOf("어둡", "차분", "밤", "dark")
    val bright = listOf("맑", "밝", "선명", "bright", "clear")
    val lowFatigue = listOf("자극 적게", "피로 적게", "편안", "잔잔", "긴장 낮게")

    fun String.hasAny(keywords: List<String>): Boolean {
        val source = lowercase()
        return keywords.any { source.contains(it.lowercase()) }
    }
}
