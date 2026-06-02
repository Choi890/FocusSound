package com.focussound.music.melody

import com.focussound.music.form.SectionType
import com.focussound.music.model.MusicTask
import kotlin.random.Random

class PhraseVariationEngine {
    fun vary(motif: Motif, task: MusicTask, sectionType: SectionType, random: Random): Motif {
        if (task == MusicTask.SLEEP) {
            return motif.copy(rhythms = motif.rhythms.map { it * 1.25f })
        }
        return when (sectionType) {
            SectionType.A -> motif
            SectionType.A_VARIATION -> motif.copy(
                degrees = motif.degrees.dropLast(1) + ((motif.degrees.last() + random.nextInt(-1, 2)).coerceIn(-2, 8))
            )
            SectionType.B -> motif.copy(
                degrees = motif.degrees.map { it + 2 },
                rhythms = motif.rhythms.reversed()
            )
            SectionType.BREAK -> motif.copy(degrees = motif.degrees.take(2), rhythms = motif.rhythms.map { it * 1.5f })
            SectionType.INTRO,
            SectionType.OUTRO -> motif.copy(degrees = motif.degrees.take(2), rhythms = motif.rhythms.map { it * 2f })
        }
    }
}
