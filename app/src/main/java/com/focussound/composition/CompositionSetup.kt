package com.focussound.composition

import com.focussound.data.FocusMode
import com.focussound.data.SoundType
import com.focussound.instrument.InstrumentPreset

enum class CompositionMood(val label: String) {
    DAWN("새벽"),
    CALM("차분함"),
    WARM("따뜻함"),
    DARK("어두움"),
    RAINY("비 오는 느낌"),
    SPACIOUS("공간감"),
    DREAMY("몽환적")
}

enum class CompositionStyle(val label: String, val genre: CompositionGenre) {
    AMBIENT("앰비언트", CompositionGenre.AMBIENT_CODING),
    LOFI("로파이", CompositionGenre.LOFI),
    CLASSICAL_MINIMAL("클래식 미니멀", CompositionGenre.CLASSICAL_MINIMAL),
    ORCHESTRAL_PAD("오케스트라 패드", CompositionGenre.ORCHESTRAL_PAD),
    PIANO_SOLO("피아노 솔로", CompositionGenre.CLASSICAL_MINIMAL),
    SLEEP_DRONE("수면 드론", CompositionGenre.SLEEP_DRONE)
}

enum class FocusIntensity(val label: String) {
    LOW("낮은 자극"),
    MEDIUM("보통"),
    CLEAR("조금 선명함")
}

data class CompositionSetup(
    val selectedInstruments: List<InstrumentPreset> = emptyList(),
    val mood: CompositionMood = CompositionMood.CALM,
    val style: CompositionStyle = CompositionStyle.AMBIENT,
    val soundType: SoundType = SoundType.NONE,
    val focusIntensity: FocusIntensity = FocusIntensity.LOW,
    val mode: FocusMode = FocusMode.STUDY,
    val loopBars: Int = 16
)
