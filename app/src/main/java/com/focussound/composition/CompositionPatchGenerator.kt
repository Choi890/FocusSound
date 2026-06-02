package com.focussound.composition

import com.focussound.condition.UserCondition
import com.focussound.personalization.UserSoundTasteVector

class CompositionPatchGenerator(
    private val recommender: CompositionRecommender = CompositionRecommender(),
    private val chordGenerator: ChordProgressionGenerator = ChordProgressionGenerator(),
    private val melodyGenerator: MelodyGenerator = MelodyGenerator(),
    private val bassGenerator: BassLineGenerator = BassLineGenerator(),
    private val padArranger: PadArranger = PadArranger(),
    private val rhythmGenerator: RhythmGenerator = RhythmGenerator(),
    private val fatigueArranger: FatigueAwareArranger = FatigueAwareArranger()
) {
    fun generate(
        intent: CompositionIntent,
        taste: UserSoundTasteVector?,
        condition: UserCondition
    ): CompositionPatch {
        val recommended = recommender.adjustIntent(intent, taste, condition)
        val random = recommended.newGenerationRandom()
        val variantNumber = compositionVariantNumber(random)
        val adjusted = recommended.withGenerationVariation(random)
        val (key, chords) = chordGenerator.generate(adjusted, random)
        val notes = padArranger.arrange(key, chords, adjusted) +
            bassGenerator.generate(key, chords, adjusted) +
            melodyGenerator.generate(key, chords, adjusted, random) +
            rhythmGenerator.generate(adjusted)
        val patch = CompositionPatch(
            name = "${buildName(adjusted)} $variantNumber",
            mode = adjusted.mode,
            genre = adjusted.genre,
            tempoBpm = adjusted.tempoHintBpm ?: 70,
            key = key,
            chordProgression = chords,
            notes = notes.sortedBy { it.startBeat },
            melodyDensity = adjusted.melodyDensityHint ?: 0.22f,
            rhythmDensity = adjusted.rhythmDensityHint ?: 0.12f,
            harmonicComplexity = adjusted.harmonicComplexityHint ?: 0.28f,
            padAmount = adjusted.padAmountHint ?: if (adjusted.padFocus) 0.72f else 0.48f,
            moodKeywords = adjusted.moodKeywords,
            fatigueScore = 40,
            durationMinutes = adjusted.durationMinutes ?: when (adjusted.mode) {
                com.focussound.data.FocusMode.SLEEP -> 45
                com.focussound.data.FocusMode.READING -> 30
                else -> 50
            }
        )
        return fatigueArranger.arrange(adjusted, patch)
    }

    private fun buildName(intent: CompositionIntent): String {
        return when (intent.genre) {
            CompositionGenre.LOFI -> "부드러운 로파이 집중곡"
            CompositionGenre.CLASSICAL_MINIMAL -> "미니멀 공부 모티프"
            CompositionGenre.ORCHESTRAL_PAD -> "오케스트라 패드 집중곡"
            CompositionGenre.SLEEP_DRONE -> "수면 드론 화성"
            CompositionGenre.AMBIENT_CODING -> "새벽 코딩 모티프"
        }
    }
}
