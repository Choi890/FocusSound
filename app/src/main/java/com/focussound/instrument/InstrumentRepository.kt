package com.focussound.instrument

import android.content.Context
import com.focussound.database.InstrumentDao
import com.focussound.database.toEntity
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InstrumentRepository(
    private val context: Context,
    private val instrumentDao: InstrumentDao
) {
    private val appContext = context.applicationContext

    private val uiowaLicense = InstrumentLicenseInfo(
        licenseName = "University of Iowa MIS",
        sourceName = "University of Iowa Musical Instrument Samples",
        sourceUrl = "https://theremin.music.uiowa.edu/MIS.html",
        redistributionAllowed = true,
        commercialUseAllowed = true,
        attributionRequired = false,
        notes = "라이선스 안내가 있는 공개 악기 샘플을 짧은 WAV 내장 샘플로 변환했습니다. 공개 배포 전 원문을 다시 확인하세요."
    )

    private val vscoLicense = InstrumentLicenseInfo(
        licenseName = "Creative Commons Zero",
        sourceName = "VSCO 2 Community Edition",
        sourceUrl = "https://versilian-studios.com/vsco-community/",
        redistributionAllowed = true,
        commercialUseAllowed = true,
        attributionRequired = false,
        notes = "Versilian Studios가 CC0로 공개한 VSCO 2 CE 샘플 중 일부를 로컬 재생용으로 내장했습니다."
    )

    val builtInPresets: List<InstrumentPreset> by lazy { buildBuiltInPresets() }

    val presets: Flow<List<InstrumentPreset>> = instrumentDao.observePresets().map { imported ->
        builtInPresets + imported.map { it.toDomain(instrumentDao.getZones(it.id)) }
    }

    suspend fun saveImported(preset: InstrumentPreset) {
        instrumentDao.upsertInstrument(
            preset = preset.toEntity(),
            zones = preset.sampleZones.map { it.toEntity(preset.id) }
        )
    }

    fun getSynthFallback(name: String, role: InstrumentRole): InstrumentPreset {
        return builtInPresets.firstOrNull { it.name == name } ?: synthFallback(name, role, 0.35f, 0.7f)
    }

    private fun buildBuiltInPresets(): List<InstrumentPreset> {
        val pianoZones = uprightPianoZones()
        val stringZones = orchestralStringZones()
        val bassZones = contrabassZones()
        val fluteZones = fluteZones()

        return listOf(
            InstrumentPreset(
                id = "builtin_vsco_upright_piano",
                name = "리얼 업라이트 피아노",
                role = InstrumentRole.MELODY,
                category = InstrumentCategory.PIANO,
                sourceType = InstrumentSourceType.BUILT_IN_WAV,
                sourcePath = pianoZones.firstOrNull()?.samplePath,
                sampleZones = pianoZones,
                adsr = AdsrParams(10, 240, 0.52f, 900),
                defaultVolume = 0.82f,
                brightness = 0.5f,
                warmth = 0.66f,
                coldness = 0.18f,
                license = vscoLicense
            ),
            InstrumentPreset(
                id = "builtin_vsco_felt_piano",
                name = "펠트 피아노",
                role = InstrumentRole.MELODY,
                category = InstrumentCategory.PIANO,
                sourceType = InstrumentSourceType.BUILT_IN_WAV,
                sourcePath = pianoZones.firstOrNull()?.samplePath,
                sampleZones = pianoZones.filter { it.maxVelocity <= 72 },
                adsr = AdsrParams(18, 320, 0.42f, 1200),
                defaultVolume = 0.68f,
                brightness = 0.34f,
                warmth = 0.78f,
                coldness = 0.08f,
                license = vscoLicense
            ),
            InstrumentPreset(
                id = "builtin_vsco_chamber_strings",
                name = "리얼 챔버 현악",
                role = InstrumentRole.PAD,
                category = InstrumentCategory.STRINGS,
                sourceType = InstrumentSourceType.BUILT_IN_WAV,
                sourcePath = stringZones.firstOrNull()?.samplePath,
                sampleZones = stringZones,
                adsr = AdsrParams(780, 520, 0.82f, 2200),
                defaultVolume = 0.76f,
                brightness = 0.34f,
                warmth = 0.84f,
                coldness = 0.12f,
                license = vscoLicense
            ),
            InstrumentPreset(
                id = "builtin_vsco_wide_string_pad",
                name = "넓은 현악 패드",
                role = InstrumentRole.PAD,
                category = InstrumentCategory.STRINGS,
                sourceType = InstrumentSourceType.BUILT_IN_WAV,
                sourcePath = stringZones.firstOrNull()?.samplePath,
                sampleZones = stringZones,
                adsr = AdsrParams(1600, 700, 0.76f, 3200),
                defaultVolume = 0.64f,
                brightness = 0.26f,
                warmth = 0.88f,
                coldness = 0.08f,
                license = vscoLicense
            ),
            InstrumentPreset(
                id = "builtin_vsco_contrabass",
                name = "리얼 콘트라베이스",
                role = InstrumentRole.BASS,
                category = InstrumentCategory.BASS,
                sourceType = InstrumentSourceType.BUILT_IN_WAV,
                sourcePath = bassZones.firstOrNull()?.samplePath,
                sampleZones = bassZones,
                adsr = AdsrParams(45, 220, 0.78f, 700),
                defaultVolume = 0.7f,
                brightness = 0.22f,
                warmth = 0.78f,
                coldness = 0.1f,
                license = vscoLicense
            ),
            InstrumentPreset(
                id = "builtin_vsco_soft_flute",
                name = "부드러운 플루트",
                role = InstrumentRole.MELODY,
                category = InstrumentCategory.WOODWIND,
                sourceType = InstrumentSourceType.BUILT_IN_WAV,
                sourcePath = fluteZones.firstOrNull()?.samplePath,
                sampleZones = fluteZones,
                adsr = AdsrParams(120, 220, 0.72f, 900),
                defaultVolume = 0.56f,
                brightness = 0.42f,
                warmth = 0.58f,
                coldness = 0.28f,
                license = vscoLicense
            ),
            synthFallback("약한 로파이 클릭", InstrumentRole.RHYTHM, 0.55f, 0.45f)
        )
    }

    private fun copyBuiltIn(fileName: String): File {
        val directory = File(appContext.filesDir, "builtin_instruments/uiowa").apply { mkdirs() }
        val target = File(directory, fileName)
        if (target.exists() && target.length() > 0L) return target
        appContext.assets.open("instruments/uiowa/$fileName").use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        }
        return target
    }

    private fun copyVsco(fileName: String): File {
        val directory = File(appContext.filesDir, "builtin_instruments/vsco2ce").apply { mkdirs() }
        val target = File(directory, fileName)
        if (target.exists() && target.length() > 0L) return target
        appContext.assets.open("instruments/vsco2ce/$fileName").use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        }
        return target
    }

    private fun uprightPianoZones(): List<SampleZone> {
        val roots = listOf(
            36 to "C2",
            43 to "G2",
            48 to "C3",
            55 to "G3",
            60 to "C4",
            67 to "G4",
            72 to "C5"
        )
        return roots.flatMapIndexed { index, (root, label) ->
            val min = if (index == 0) 30 else ((roots[index - 1].first + root) / 2) + 1
            val max = if (index == roots.lastIndex) 84 else (root + roots[index + 1].first) / 2
            listOf(
                SampleZone(
                    samplePath = copyVsco("UR1_${label}_pp_RR1.wav").absolutePath,
                    rootMidiNote = root,
                    minMidiNote = min,
                    maxMidiNote = max,
                    minVelocity = 1,
                    maxVelocity = 72
                ),
                SampleZone(
                    samplePath = copyVsco("UR1_${label}_mf_RR1.wav").absolutePath,
                    rootMidiNote = root,
                    minMidiNote = min,
                    maxMidiNote = max,
                    minVelocity = 73,
                    maxVelocity = 127
                )
            )
        }
    }

    private fun orchestralStringZones(): List<SampleZone> = listOf(
        SampleZone(copyVsco("susvib_A2_v1_1.wav").absolutePath, rootMidiNote = 45, minMidiNote = 40, maxMidiNote = 46),
        SampleZone(copyVsco("susvib_C3_v1_1.wav").absolutePath, rootMidiNote = 48, minMidiNote = 47, maxMidiNote = 50),
        SampleZone(copyVsco("susvib_E3_v1_1.wav").absolutePath, rootMidiNote = 52, minMidiNote = 51, maxMidiNote = 54),
        SampleZone(copyVsco("susvib_G3_v1_1.wav").absolutePath, rootMidiNote = 55, minMidiNote = 55, maxMidiNote = 58),
        SampleZone(copyVsco("ViolaEns_susvib_A3_v1_1.wav").absolutePath, rootMidiNote = 57, minMidiNote = 59, maxMidiNote = 59),
        SampleZone(copyVsco("ViolaEns_susvib_C4_v1_1.wav").absolutePath, rootMidiNote = 60, minMidiNote = 60, maxMidiNote = 62),
        SampleZone(copyVsco("ViolaEns_susvib_E4_v1_1.wav").absolutePath, rootMidiNote = 64, minMidiNote = 63, maxMidiNote = 65),
        SampleZone(copyVsco("VlnEns_susVib_C4_v1.wav").absolutePath, rootMidiNote = 60, minMidiNote = 66, maxMidiNote = 66),
        SampleZone(copyVsco("VlnEns_susVib_E4_v1.wav").absolutePath, rootMidiNote = 64, minMidiNote = 67, maxMidiNote = 68),
        SampleZone(copyVsco("VlnEns_susVib_G4_v1.wav").absolutePath, rootMidiNote = 67, minMidiNote = 69, maxMidiNote = 71),
        SampleZone(copyVsco("VlnEns_susVib_D5_v1.wav").absolutePath, rootMidiNote = 74, minMidiNote = 72, maxMidiNote = 79)
    )

    private fun contrabassZones(): List<SampleZone> = listOf(
        SampleZone(copyVsco("BKCtbss_SusNV_C1_v1_rr1.wav").absolutePath, rootMidiNote = 24, minMidiNote = 20, maxMidiNote = 26),
        SampleZone(copyVsco("BKCtbss_SusNV_E1_v1_rr1.wav").absolutePath, rootMidiNote = 28, minMidiNote = 27, maxMidiNote = 31),
        SampleZone(copyVsco("BKCtbss_SusNV_A1_v1_rr1.wav").absolutePath, rootMidiNote = 33, minMidiNote = 32, maxMidiNote = 42)
    )

    private fun fluteZones(): List<SampleZone> = listOf(
        SampleZone(copyVsco("LDFlute_susNV_C4_v1_1.wav").absolutePath, rootMidiNote = 60, minMidiNote = 56, maxMidiNote = 62),
        SampleZone(copyVsco("LDFlute_susNV_E4_v1_1.wav").absolutePath, rootMidiNote = 64, minMidiNote = 63, maxMidiNote = 66),
        SampleZone(copyVsco("LDFlute_susNV_A4_v1_1.wav").absolutePath, rootMidiNote = 69, minMidiNote = 67, maxMidiNote = 70),
        SampleZone(copyVsco("LDFlute_susNV_C5_v1_1.wav").absolutePath, rootMidiNote = 72, minMidiNote = 71, maxMidiNote = 82)
    )

    private fun synthFallback(
        name: String,
        role: InstrumentRole,
        brightness: Float,
        warmth: Float
    ): InstrumentPreset = InstrumentPreset(
        id = "builtin_${name.lowercase().replace(" ", "_")}",
        name = name,
        role = role,
        sourceType = InstrumentSourceType.SYNTH_FALLBACK,
        sourcePath = null,
        sampleZones = emptyList(),
        adsr = when (role) {
            InstrumentRole.MELODY -> AdsrParams(45, 140, 0.62f, 360)
            InstrumentRole.PAD -> AdsrParams(950, 420, 0.78f, 1800)
            InstrumentRole.BASS -> AdsrParams(35, 90, 0.7f, 180)
            InstrumentRole.RHYTHM -> AdsrParams(4, 30, 0.35f, 45)
        },
        defaultVolume = when (role) {
            InstrumentRole.MELODY -> 0.48f
            InstrumentRole.PAD -> 0.54f
            InstrumentRole.BASS -> 0.42f
            InstrumentRole.RHYTHM -> 0.24f
        },
        brightness = brightness,
        warmth = warmth,
        coldness = 0.2f,
        license = InstrumentLicenseInfo.SynthFallback
    )
}
