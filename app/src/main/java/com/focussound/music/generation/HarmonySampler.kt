package com.focussound.music.generation

import com.focussound.composition.Chord
import com.focussound.composition.ChordExtension
import com.focussound.composition.ChordQuality
import com.focussound.composition.MusicalKey
import com.focussound.composition.PitchClass
import com.focussound.composition.ScaleType
import com.focussound.music.form.MusicSection
import com.focussound.music.form.SectionType
import com.focussound.music.knowledge.MusicAvoidRule
import com.focussound.music.knowledge.TaskMusicProfile
import com.focussound.music.learning.HarmonyModel
import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask
import kotlin.random.Random

class HarmonySampler {
    fun sampleKey(request: LiveCompositionRequest, random: Random): MusicalKey {
        val candidates = when {
            request.task == MusicTask.SLEEP -> listOf(
                MusicalKey(PitchClass.C, ScaleType.AMBIENT),
                MusicalKey(PitchClass.D, ScaleType.AMBIENT),
                MusicalKey(PitchClass.A, ScaleType.MINOR),
                MusicalKey(PitchClass.F, ScaleType.MAJOR)
            )
            request.task == MusicTask.READING -> listOf(
                MusicalKey(PitchClass.C, ScaleType.AMBIENT),
                MusicalKey(PitchClass.F, ScaleType.MAJOR),
                MusicalKey(PitchClass.D, ScaleType.DORIAN)
            )
            request.task == MusicTask.WORKOUT -> listOf(
                MusicalKey(PitchClass.A, ScaleType.MINOR),
                MusicalKey(PitchClass.D, ScaleType.DORIAN),
                MusicalKey(PitchClass.G, ScaleType.MINOR),
                MusicalKey(PitchClass.E, ScaleType.MINOR)
            )
            request.style == MusicStyle.RELAXING_PIANO -> listOf(
                MusicalKey(PitchClass.D, ScaleType.MINOR),
                MusicalKey(PitchClass.C, ScaleType.MAJOR),
                MusicalKey(PitchClass.G, ScaleType.MAJOR),
                MusicalKey(PitchClass.F, ScaleType.MAJOR)
            )
            request.style == MusicStyle.ORCHESTRAL_PAD -> listOf(
                MusicalKey(PitchClass.F, ScaleType.MAJOR),
                MusicalKey(PitchClass.D, ScaleType.DORIAN),
                MusicalKey(PitchClass.C, ScaleType.MAJOR),
                MusicalKey(PitchClass.A, ScaleType.MINOR)
            )
            else -> listOf(
                MusicalKey(PitchClass.D, ScaleType.DORIAN),
                MusicalKey(PitchClass.A, ScaleType.MINOR),
                MusicalKey(PitchClass.E, ScaleType.MINOR),
                MusicalKey(PitchClass.G, ScaleType.MAJOR)
            )
        }
        return candidates.random(random)
    }

    fun sampleSection(
        request: LiveCompositionRequest,
        profile: TaskMusicProfile,
        model: HarmonyModel,
        key: MusicalKey,
        section: MusicSection,
        temperature: GenerationTemperature,
        random: Random,
        previous: List<Chord>
    ): List<Chord> {
        val template = model.degreeTemplates.weightedRandom(random) {
            it.probability + random.nextFloat() * temperature.harmonyTemperature
        }
        var degrees = template.degrees
        if (section.type == SectionType.B && random.nextFloat() < 0.62f + temperature.harmonyTemperature * 0.2f) {
            degrees = degrees.mapIndexed { index, degree ->
                if (index % 2 == 0) (degree + 2).floorMod(7) else degree
            }
        }
        if (section.type == SectionType.A_VARIATION && random.nextFloat() < 0.5f + temperature.harmonyTemperature * 0.25f) {
            degrees = degrees.drop(1) + degrees.first()
        }
        if (previous.isNotEmpty() && random.nextFloat() < temperature.harmonyTemperature * 0.35f) {
            degrees = degrees.toMutableList().also { it[0] = previous.last().degree }
        }
        if (MusicAvoidRule.FAST_HARMONIC_RHYTHM in profile.avoidRules && section.type != SectionType.B) {
            degrees = degrees.take(2).flatMap { listOf(it, it) }
        }

        val bars = section.bars.coerceAtLeast(4)
        return List(bars) { bar ->
            val degree = degrees[bar % degrees.size]
            val resolvedDegree = if (bar == bars - 1 && random.nextFloat() < 0.55f) {
                model.cadenceDegrees.random(random)
            } else {
                degree
            }
            chordForDegree(
                key = key,
                degree = resolvedDegree,
                extensionProbability = model.extensionProbability + temperature.harmonyTemperature * 0.2f,
                request = request,
                random = random
            )
        }
    }

    private fun chordForDegree(
        key: MusicalKey,
        degree: Int,
        extensionProbability: Float,
        request: LiveCompositionRequest,
        random: Random
    ): Chord {
        val scale = key.scaleSemitones()
        val root = key.root.transpose(scale[degree.floorMod(scale.size)])
        val quality = qualityFor(key.scaleType, degree)
        val extension = when {
            request.task == MusicTask.WORKOUT && random.nextFloat() > 0.35f -> ChordExtension.NONE
            random.nextFloat() < extensionProbability.coerceIn(0f, 0.85f) -> listOf(
                ChordExtension.SIXTH,
                ChordExtension.SEVENTH,
                ChordExtension.NINTH
            ).random(random)
            else -> ChordExtension.NONE
        }
        return Chord(root = root, quality = quality, extension = extension, degree = degree.floorMod(scale.size))
    }

    private fun qualityFor(scaleType: ScaleType, degree: Int): ChordQuality = when (scaleType) {
        ScaleType.MAJOR -> if (degree.floorMod(7) in listOf(1, 2, 5)) ChordQuality.MINOR else ChordQuality.MAJOR
        ScaleType.MINOR, ScaleType.AMBIENT -> if (degree.floorMod(7) in listOf(2, 5)) ChordQuality.MAJOR else ChordQuality.MINOR
        ScaleType.DORIAN -> if (degree.floorMod(7) in listOf(0, 1, 4)) ChordQuality.MINOR else ChordQuality.MAJOR
        ScaleType.PENTATONIC -> ChordQuality.SUS2
    }
}

private fun Int.floorMod(other: Int): Int = Math.floorMod(this, other)
