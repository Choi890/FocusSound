package com.focussound.music.generation

import com.focussound.music.form.MusicForm
import com.focussound.music.form.MusicSection
import com.focussound.music.form.SectionType
import com.focussound.music.knowledge.TaskMusicProfile
import com.focussound.music.learning.FormModel
import com.focussound.music.learning.WeightedFormTemplate
import com.focussound.music.model.MusicTask
import kotlin.random.Random

class FormSampler {
    fun sample(
        profile: TaskMusicProfile,
        model: FormModel,
        temperature: GenerationTemperature,
        random: Random
    ): MusicForm {
        val template = model.templates.weightedRandom(random) { it.probability + random.nextFloat() * temperature.formTemperature }
        val sections = template.sections.mapIndexed { index, type ->
            MusicSection(
                id = "${type.label} ${index + 1}",
                type = type,
                bars = barsFor(type, profile, temperature, random),
                energy = energyFor(profile.task, type, index, temperature),
                variationLevel = variationFor(type, temperature)
            )
        }
        return MusicForm(sections)
    }

    private fun barsFor(
        type: SectionType,
        profile: TaskMusicProfile,
        temperature: GenerationTemperature,
        random: Random
    ): Int {
        val base = when (type) {
            SectionType.INTRO -> if (profile.task == MusicTask.SLEEP) 8 else 4
            SectionType.OUTRO -> if (profile.task == MusicTask.SLEEP) 12 else 4
            SectionType.BREAK -> 4
            else -> random.nextInt(profile.sectionLengthBars.first, profile.sectionLengthBars.last + 1)
        }
        val quantized = when {
            profile.task == MusicTask.SLEEP -> (base / 8).coerceAtLeast(1) * 8
            profile.task == MusicTask.READING -> (base / 4).coerceAtLeast(2) * 4
            else -> (base / 4).coerceAtLeast(1) * 4
        }
        val extra = if (temperature.formTemperature > 0.68f && random.nextFloat() < 0.25f) 4 else 0
        return (quantized + extra).coerceIn(4, 64)
    }

    private fun energyFor(task: MusicTask, type: SectionType, index: Int, temperature: GenerationTemperature): Float {
        val base = when (type) {
            SectionType.INTRO -> 0.18f
            SectionType.A -> 0.34f
            SectionType.A_VARIATION -> 0.42f
            SectionType.B -> 0.58f
            SectionType.BREAK -> 0.28f
            SectionType.OUTRO -> 0.16f
        }
        val taskScale = when (task) {
            MusicTask.SLEEP -> 0.52f
            MusicTask.READING -> 0.58f
            MusicTask.STUDY -> 0.72f
            MusicTask.RELAX -> 0.68f
            MusicTask.CODING -> 0.82f
            MusicTask.WORKOUT -> 1.18f
        }
        return (base * taskScale + index * 0.015f * temperature.formTemperature).coerceIn(0.08f, 0.92f)
    }

    private fun variationFor(type: SectionType, temperature: GenerationTemperature): Float {
        val base = when (type) {
            SectionType.INTRO -> 0.1f
            SectionType.A -> 0.22f
            SectionType.A_VARIATION -> 0.48f
            SectionType.B -> 0.62f
            SectionType.BREAK -> 0.38f
            SectionType.OUTRO -> 0.16f
        }
        return (base + temperature.formTemperature * 0.18f).coerceIn(0f, 1f)
    }
}

internal fun <T> List<T>.weightedRandom(random: Random, weight: (T) -> Float): T {
    if (isEmpty()) error("Cannot sample from an empty list.")
    val total = sumOf { weight(it).coerceAtLeast(0.001f).toDouble() }.toFloat()
    var cursor = random.nextFloat() * total
    for (item in this) {
        cursor -= weight(item).coerceAtLeast(0.001f)
        if (cursor <= 0f) return item
    }
    return last()
}
