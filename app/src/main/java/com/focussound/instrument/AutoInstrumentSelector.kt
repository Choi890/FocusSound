package com.focussound.instrument

import com.focussound.composition.CompositionIntent
import com.focussound.data.FocusMode

class AutoInstrumentSelector {
    fun selectFor(
        intent: CompositionIntent,
        available: List<InstrumentPreset>,
        policy: InstrumentPolicy = InstrumentPolicy.AUTO_REAL_SAMPLES
    ): InstrumentSet {
        val useImportedFirst = policy == InstrumentPolicy.USER_IMPORTED_SAMPLES_FIRST ||
            policy == InstrumentPolicy.AUTO_REAL_SAMPLES
        fun find(name: String, role: InstrumentRole): InstrumentPreset {
            val candidates = available.filter { it.role == role }
            val imported = candidates.firstOrNull {
                useImportedFirst &&
                    it.sourceType == InstrumentSourceType.USER_IMPORTED_WAV &&
                    it.name.contains(name, ignoreCase = true)
            }
            val anyImported = candidates.firstOrNull {
                useImportedFirst && it.sourceType == InstrumentSourceType.USER_IMPORTED_WAV
            }
            return imported
                ?: anyImported
                ?: candidates.firstOrNull { it.name == name }
                ?: candidates.firstOrNull { it.sourceType == InstrumentSourceType.SYNTH_FALLBACK }
                ?: InstrumentRepositoryFallback.preset(name, role)
        }

        fun findOrNull(name: String, role: InstrumentRole): InstrumentPreset? {
            return available.firstOrNull { it.role == role && it.name == name }
        }

        return when (intent.mode) {
            FocusMode.CODING -> InstrumentSet(
                melody = find("부드러운 피아노", InstrumentRole.MELODY),
                pad = find("따뜻한 패드", InstrumentRole.PAD),
                bass = find("서브 베이스", InstrumentRole.BASS),
                rhythm = findOrNull("약한 로파이 클릭", InstrumentRole.RHYTHM)
            )

            FocusMode.STUDY -> InstrumentSet(
                melody = find("부드러운 피아노", InstrumentRole.MELODY),
                pad = find("부드러운 현악", InstrumentRole.PAD),
                bass = find("따뜻한 베이스", InstrumentRole.BASS),
                rhythm = null
            )

            FocusMode.READING -> InstrumentSet(
                melody = null,
                pad = find("부드러운 현악", InstrumentRole.PAD),
                bass = find("따뜻한 베이스", InstrumentRole.BASS),
                rhythm = null
            )

            FocusMode.SLEEP -> InstrumentSet(
                melody = null,
                pad = find("어두운 패드", InstrumentRole.PAD),
                bass = find("서브 베이스", InstrumentRole.BASS),
                rhythm = null
            )
        }
    }
}

private object InstrumentRepositoryFallback {
    fun preset(name: String, role: InstrumentRole): InstrumentPreset = InstrumentPreset(
        id = "fallback_${name.lowercase().replace(" ", "_")}",
        name = name,
        role = role,
        sourceType = InstrumentSourceType.SYNTH_FALLBACK,
        sourcePath = null,
        sampleZones = emptyList(),
        adsr = AdsrParams(120, 160, 0.6f, 480),
        defaultVolume = 0.45f,
        brightness = 0.35f,
        warmth = 0.7f
    )
}
