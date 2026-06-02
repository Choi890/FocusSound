package com.focussound.composition

import com.focussound.data.FocusMode
import kotlin.math.roundToInt
import kotlin.random.Random

internal fun CompositionIntent.newGenerationRandom(): Random {
    val seed = System.nanoTime() xor
        (System.currentTimeMillis() shl 11) xor
        hashCode().toLong()
    return Random(seed)
}

internal fun CompositionIntent.withGenerationVariation(random: Random): CompositionIntent {
    val tempoBase = tempoHintBpm ?: defaultTempo()
    val tempoRange = when (mode) {
        FocusMode.SLEEP -> -3..2
        FocusMode.STUDY,
        FocusMode.READING -> -5..5
        FocusMode.CODING -> -6..7
    }
    val melodyMultiplier = when (genre) {
        CompositionGenre.SLEEP_DRONE -> 0.35f
        CompositionGenre.ORCHESTRAL_PAD -> random.nextFloatIn(0.72f, 1.12f)
        CompositionGenre.CLASSICAL_MINIMAL -> random.nextFloatIn(0.78f, 1.35f)
        CompositionGenre.LOFI -> random.nextFloatIn(0.7f, 1.25f)
        CompositionGenre.AMBIENT_CODING -> random.nextFloatIn(0.68f, 1.18f)
    }
    val rhythmMultiplier = when (genre) {
        CompositionGenre.LOFI -> random.nextFloatIn(0.72f, 1.28f)
        CompositionGenre.SLEEP_DRONE -> 0.2f
        else -> random.nextFloatIn(0.55f, 1.15f)
    }
    val padMultiplier = when {
        mode == FocusMode.SLEEP -> random.nextFloatIn(0.92f, 1.12f)
        padFocus -> random.nextFloatIn(0.84f, 1.18f)
        else -> random.nextFloatIn(0.72f, 1.08f)
    }

    return copy(
        tempoHintBpm = (tempoBase + random.nextInt(tempoRange.first, tempoRange.last + 1))
            .coerceIn(48, 92),
        melodyDensityHint = melodyDensityHint
            ?.let { (it * melodyMultiplier + random.nextFloatIn(-0.025f, 0.025f)).coerceIn(0.02f, 0.68f) },
        rhythmDensityHint = rhythmDensityHint
            ?.let { (it * rhythmMultiplier + random.nextFloatIn(-0.012f, 0.016f)).coerceIn(0.01f, 0.34f) },
        harmonicComplexityHint = harmonicComplexityHint
            ?.let { (it + random.nextFloatIn(-0.08f, 0.1f)).coerceIn(0.08f, 0.72f) },
        padAmountHint = padAmountHint
            ?.let { (it * padMultiplier + random.nextFloatIn(-0.04f, 0.04f)).coerceIn(0.12f, 1f) }
    )
}

internal fun compositionVariantNumber(random: Random): Int = random.nextInt(100, 1000)

private fun CompositionIntent.defaultTempo(): Int = when (genre) {
    CompositionGenre.SLEEP_DRONE -> 54
    CompositionGenre.LOFI -> 74
    CompositionGenre.CLASSICAL_MINIMAL -> 70
    CompositionGenre.ORCHESTRAL_PAD -> 62
    CompositionGenre.AMBIENT_CODING -> 66
}

private fun Random.nextFloatIn(min: Float, max: Float): Float {
    return min + (max - min) * nextFloat()
}
