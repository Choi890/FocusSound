package com.focussound.audio.instrument

import com.focussound.instrument.InstrumentPreset
import com.focussound.instrument.InstrumentRole

class InstrumentLibrary {
    fun recommendedFor(role: InstrumentRole, instruments: List<InstrumentPreset>): InstrumentPreset? {
        return instruments.firstOrNull { it.role == role && it.isSampleBacked }
            ?: instruments.firstOrNull { it.role == role }
    }
}
