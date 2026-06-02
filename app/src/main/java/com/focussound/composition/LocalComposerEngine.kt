package com.focussound.composition

import com.focussound.condition.UserCondition
import com.focussound.data.FocusMode
import com.focussound.personalization.LocalCompositionRecommender
import com.focussound.personalization.UserSoundTasteVector

class LocalComposerEngine(
    private val recommender: LocalCompositionRecommender = LocalCompositionRecommender(),
    private val chordGenerator: ChordProgressionGenerator = ChordProgressionGenerator(),
    private val melodyGenerator: MelodyGenerator = MelodyGenerator(),
    private val bassGenerator: BassLineGenerator = BassLineGenerator(),
    private val padArranger: PadArranger = PadArranger(),
    private val rhythmGenerator: RhythmGenerator = RhythmGenerator(),
    private val fatigueArranger: FatigueAwareArranger = FatigueAwareArranger()
) {
    fun compose(
        intent: CompositionIntent,
        taste: UserSoundTasteVector?,
        condition: UserCondition
    ): CompositionPatch {
        val recommended = recommender.applyTasteAndCondition(intent, taste, condition)
        val random = recommended.newGenerationRandom()
        val variantNumber = compositionVariantNumber(random)
        val adjusted = recommended.withGenerationVariation(random)
        val (key, chords) = chordGenerator.generate(adjusted, random)
        val notes = padArranger.arrange(key, chords, adjusted) +
            bassGenerator.generate(key, chords, adjusted) +
            melodyGenerator.generate(key, chords, adjusted, random) +
            rhythmGenerator.generate(adjusted)
        val patch = CompositionPatch(
            name = "${buildLocalName(adjusted)} $variantNumber",
            mode = adjusted.mode,
            genre = adjusted.genre,
            tempoBpm = adjusted.tempoHintBpm ?: 68,
            key = key,
            chordProgression = chords,
            notes = notes.sortedWith(compareBy<NoteEvent> { it.startBeat }.thenBy { it.lane.ordinal }),
            melodyDensity = adjusted.melodyDensityHint ?: 0.2f,
            rhythmDensity = adjusted.rhythmDensityHint ?: 0.08f,
            harmonicComplexity = adjusted.harmonicComplexityHint ?: 0.28f,
            padAmount = adjusted.padAmountHint ?: if (adjusted.padFocus) 0.72f else 0.52f,
            moodKeywords = adjusted.moodKeywords,
            fatigueScore = 40,
            durationMinutes = adjusted.durationMinutes ?: when (adjusted.mode) {
                FocusMode.SLEEP -> 45
                FocusMode.READING -> 30
                else -> 50
            }
        )
        return fatigueArranger.arrange(adjusted, patch)
    }

    private fun buildLocalName(intent: CompositionIntent): String {
        return when (intent.genre) {
            CompositionGenre.LOFI -> "로컬 로파이 집중곡"
            CompositionGenre.CLASSICAL_MINIMAL -> "로컬 미니멀 공부곡"
            CompositionGenre.ORCHESTRAL_PAD -> "로컬 오케스트라 패드"
            CompositionGenre.SLEEP_DRONE -> "로컬 수면 드론"
            CompositionGenre.AMBIENT_CODING -> "로컬 새벽 코딩곡"
        }
    }
}
