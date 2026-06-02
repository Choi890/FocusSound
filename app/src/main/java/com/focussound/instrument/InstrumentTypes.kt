package com.focussound.instrument

import java.util.UUID

enum class InstrumentRole {
    MELODY,
    PAD,
    BASS,
    RHYTHM
}

enum class InstrumentCategory {
    PIANO,
    STRINGS,
    BASS,
    PAD,
    WOODWIND,
    BRASS,
    PERCUSSION
}

enum class InstrumentSourceType {
    BUILT_IN_WAV,
    USER_IMPORTED_WAV,
    USER_IMPORTED_SF2,
    SYNTH_FALLBACK
}

enum class InstrumentPolicy {
    AUTO_REAL_SAMPLES,
    BUILT_IN_SAMPLES_ONLY,
    USER_IMPORTED_SAMPLES_FIRST,
    SYNTH_FALLBACK
}

data class AdsrParams(
    val attackMillis: Int,
    val decayMillis: Int,
    val sustainLevel: Float,
    val releaseMillis: Int
)

data class SampleZone(
    val samplePath: String,
    val rootMidiNote: Int,
    val minMidiNote: Int,
    val maxMidiNote: Int,
    val minVelocity: Int = 1,
    val maxVelocity: Int = 127,
    val loopStartFrame: Int? = null,
    val loopEndFrame: Int? = null
)

data class InstrumentLicenseInfo(
    val licenseName: String,
    val sourceName: String,
    val sourceUrl: String?,
    val redistributionAllowed: Boolean,
    val commercialUseAllowed: Boolean,
    val attributionRequired: Boolean,
    val notes: String
) {
    companion object {
        val UserOwned = InstrumentLicenseInfo(
            licenseName = "사용자 제공",
            sourceName = "사용자 파일",
            sourceUrl = null,
            redistributionAllowed = false,
            commercialUseAllowed = false,
            attributionRequired = false,
            notes = "사용자가 직접 가져온 파일입니다. 사용 권한은 사용자 확인이 필요합니다."
        )

        val SynthFallback = InstrumentLicenseInfo(
            licenseName = "앱 내부 신스",
            sourceName = "FocusSound",
            sourceUrl = null,
            redistributionAllowed = true,
            commercialUseAllowed = true,
            attributionRequired = false,
            notes = "샘플 파일 없이 앱 내부 합성으로 생성됩니다."
        )
    }
}

data class InstrumentPreset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: InstrumentRole,
    val category: InstrumentCategory = role.defaultCategory(),
    val sourceType: InstrumentSourceType,
    val sourcePath: String?,
    val sampleZones: List<SampleZone>,
    val adsr: AdsrParams,
    val defaultVolume: Float,
    val brightness: Float,
    val warmth: Float,
    val coldness: Float = 0.2f,
    val license: InstrumentLicenseInfo = InstrumentLicenseInfo.UserOwned,
    val importedAtMillis: Long = System.currentTimeMillis()
) {
    val isSampleBacked: Boolean
        get() = sourceType == InstrumentSourceType.BUILT_IN_WAV || sourceType == InstrumentSourceType.USER_IMPORTED_WAV
}

private fun InstrumentRole.defaultCategory(): InstrumentCategory = when (this) {
    InstrumentRole.MELODY -> InstrumentCategory.PIANO
    InstrumentRole.PAD -> InstrumentCategory.PAD
    InstrumentRole.BASS -> InstrumentCategory.BASS
    InstrumentRole.RHYTHM -> InstrumentCategory.PERCUSSION
}

data class InstrumentSet(
    val melody: InstrumentPreset?,
    val pad: InstrumentPreset?,
    val bass: InstrumentPreset?,
    val rhythm: InstrumentPreset?
) {
    val names: List<String>
        get() = listOfNotNull(melody?.name, pad?.name, bass?.name, rhythm?.name)
}
