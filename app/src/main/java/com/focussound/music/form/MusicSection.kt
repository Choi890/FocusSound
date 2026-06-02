package com.focussound.music.form

enum class SectionType(val label: String) {
    INTRO("인트로"),
    A("A"),
    A_VARIATION("A'"),
    B("B"),
    BREAK("브레이크"),
    OUTRO("아웃트로")
}

data class MusicSection(
    val id: String,
    val type: SectionType,
    val bars: Int,
    val energy: Float,
    val variationLevel: Float
)

data class MusicForm(
    val sections: List<MusicSection>
) {
    val totalBars: Int
        get() = sections.sumOf { it.bars }
}
