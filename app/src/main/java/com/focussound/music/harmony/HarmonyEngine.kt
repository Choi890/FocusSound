package com.focussound.music.harmony

import com.focussound.composition.Chord
import com.focussound.composition.MusicalKey
import com.focussound.music.form.MusicSection
import com.focussound.music.form.SectionType
import com.focussound.music.model.LiveCompositionRequest
import kotlin.random.Random

class HarmonyEngine(
    private val library: ChordProgressionLibrary = ChordProgressionLibrary()
) {
    fun chooseKey(request: LiveCompositionRequest, random: Random): MusicalKey {
        return library.chooseKey(request.task, request.style, random)
    }

    fun generateSectionChords(
        request: LiveCompositionRequest,
        key: MusicalKey,
        section: MusicSection,
        random: Random
    ): List<Chord> {
        val base = library.progression(request.task, request.style, key, random)
        val bars = section.bars.coerceAtLeast(4)
        val repeated = List(bars) { bar ->
            val source = base[bar % base.size]
            when {
                section.type == SectionType.A_VARIATION && bar % 4 == 3 -> source.copy(extension = source.extension)
                section.type == SectionType.B && bar % 2 == 0 -> base[(bar + 1) % base.size]
                else -> source
            }
        }
        return repeated
    }
}
