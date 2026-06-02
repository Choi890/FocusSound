package com.focussound.instrument

interface SoundFontInstrumentProvider {
    fun isAvailable(): Boolean
    fun load(preset: InstrumentPreset): Result<Unit>
}

class DisabledSoundFontInstrumentProvider : SoundFontInstrumentProvider {
    override fun isAvailable(): Boolean = false
    override fun load(preset: InstrumentPreset): Result<Unit> {
        return Result.failure(UnsupportedOperationException("SF2 재생에는 선택형 로컬 신스 모듈이 필요합니다."))
    }
}
