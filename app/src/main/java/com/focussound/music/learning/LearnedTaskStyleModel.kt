package com.focussound.music.learning

import com.focussound.music.form.SectionType
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask

data class LearnedTaskStyleModel(
    val task: MusicTask,
    val style: MusicStyle,
    val formModel: FormModel,
    val harmonyModel: HarmonyModel,
    val motifModel: MotifModel,
    val rhythmModel: RhythmModel,
    val arrangementModel: ArrangementModel,
    val transitionModel: SectionTransitionModel,
    val toneTarget: ToneTarget
)

data class FormModel(
    val templates: List<WeightedFormTemplate>
)

data class WeightedFormTemplate(
    val sections: List<SectionType>,
    val probability: Float
)

data class HarmonyModel(
    val degreeTemplates: List<WeightedDegreeTemplate>,
    val cadenceDegrees: List<Int>,
    val extensionProbability: Float
)

data class WeightedDegreeTemplate(
    val degrees: List<Int>,
    val probability: Float
)

data class MotifModel(
    val contourTemplates: List<WeightedMotifTemplate>
)

data class WeightedMotifTemplate(
    val intervals: List<Int>,
    val rhythms: List<Float>,
    val probability: Float
)

data class RhythmModel(
    val allowedRhythms: List<Float>,
    val syncopation: Float
)

data class ArrangementModel(
    val pianoWeight: Float,
    val padWeight: Float,
    val stringWeight: Float,
    val bassWeight: Float,
    val rhythmWeight: Float
)

data class SectionTransitionModel(
    val transitionNoteProbability: Float,
    val energyStepLimit: Float
)

data class ToneTarget(
    val brightness: Float,
    val warmth: Float,
    val motion: Float
)
