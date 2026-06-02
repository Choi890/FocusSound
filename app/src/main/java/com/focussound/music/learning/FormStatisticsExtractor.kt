package com.focussound.music.learning

import com.focussound.music.form.SectionType
import com.focussound.music.knowledge.FormType
import com.focussound.music.knowledge.TaskMusicProfile
import com.focussound.music.model.MusicTask

class FormStatisticsExtractor {
    fun extract(profile: TaskMusicProfile, corpus: List<SymbolicMusicDocument>): FormModel {
        val corpusTemplates = corpus.mapNotNull { document ->
            val sections = document.sectionLabels.mapNotNull { label ->
                runCatching { SectionType.valueOf(label) }.getOrNull()
            }
            sections.takeIf { it.size >= 3 }?.let { WeightedFormTemplate(it, 0.35f) }
        }
        return FormModel(corpusTemplates + builtInTemplates(profile))
    }

    private fun builtInTemplates(profile: TaskMusicProfile): List<WeightedFormTemplate> = when {
        FormType.LONG_FADE in profile.allowedFormTypes -> listOf(
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.A_VARIATION, SectionType.A_VARIATION, SectionType.OUTRO), 0.55f),
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.A, SectionType.A_VARIATION, SectionType.OUTRO), 0.45f)
        )
        FormType.GROOVE_FORM in profile.allowedFormTypes && profile.task == MusicTask.WORKOUT -> listOf(
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.A_VARIATION, SectionType.B, SectionType.BREAK, SectionType.B), 0.55f),
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.B, SectionType.A_VARIATION, SectionType.B), 0.45f)
        )
        FormType.GROOVE_FORM in profile.allowedFormTypes -> listOf(
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.A_VARIATION, SectionType.BREAK, SectionType.B, SectionType.A_VARIATION), 0.52f),
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.B, SectionType.A_VARIATION, SectionType.OUTRO), 0.48f)
        )
        FormType.SLOW_EVOLUTION in profile.allowedFormTypes -> listOf(
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.A_VARIATION, SectionType.B, SectionType.OUTRO), 0.5f),
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.A_VARIATION, SectionType.A_VARIATION, SectionType.B, SectionType.OUTRO), 0.5f)
        )
        else -> listOf(
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.A_VARIATION, SectionType.B, SectionType.A_VARIATION), 0.5f),
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.B, SectionType.A_VARIATION, SectionType.OUTRO), 0.3f),
            WeightedFormTemplate(listOf(SectionType.INTRO, SectionType.A, SectionType.A_VARIATION, SectionType.BREAK, SectionType.A_VARIATION), 0.2f)
        )
    }
}
