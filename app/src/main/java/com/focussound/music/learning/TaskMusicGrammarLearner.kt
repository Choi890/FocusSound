package com.focussound.music.learning

import com.focussound.music.knowledge.TaskMusicProfileRepository
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask

class TaskMusicGrammarLearner(
    private val profileRepository: TaskMusicProfileRepository = TaskMusicProfileRepository(),
    private val formExtractor: FormStatisticsExtractor = FormStatisticsExtractor(),
    private val harmonyExtractor: HarmonyStatisticsExtractor = HarmonyStatisticsExtractor(),
    private val motifExtractor: MotifStatisticsExtractor = MotifStatisticsExtractor(),
    private val rhythmExtractor: RhythmStatisticsExtractor = RhythmStatisticsExtractor(),
    private val arrangementExtractor: ArrangementStatisticsExtractor = ArrangementStatisticsExtractor(),
    private val transitionExtractor: SectionTransitionStatisticsExtractor = SectionTransitionStatisticsExtractor()
) {
    fun learn(
        task: MusicTask,
        style: MusicStyle,
        corpus: List<SymbolicMusicDocument> = emptyList()
    ): LearnedTaskStyleModel {
        val profile = profileRepository.get(task)
        return LearnedTaskStyleModel(
            task = task,
            style = style,
            formModel = formExtractor.extract(profile, corpus),
            harmonyModel = harmonyExtractor.extract(profile, style, corpus),
            motifModel = motifExtractor.extract(profile, corpus),
            rhythmModel = rhythmExtractor.extract(profile, corpus),
            arrangementModel = arrangementExtractor.extract(profile),
            transitionModel = transitionExtractor.extract(profile),
            toneTarget = toneTargetFor(task, style)
        )
    }

    private fun toneTargetFor(task: MusicTask, style: MusicStyle): ToneTarget {
        val styleWarmth = when (style) {
            MusicStyle.RELAXING_PIANO, MusicStyle.ORCHESTRAL_PAD, MusicStyle.SLEEP_DRONE -> 0.78f
            MusicStyle.LOFI -> 0.68f
            MusicStyle.MINIMAL_ELECTRONIC -> 0.46f
            else -> 0.58f
        }
        return when (task) {
            MusicTask.SLEEP -> ToneTarget(0.22f, 0.84f, 0.06f)
            MusicTask.READING -> ToneTarget(0.26f, 0.74f, 0.08f)
            MusicTask.STUDY -> ToneTarget(0.34f, styleWarmth, 0.16f)
            MusicTask.CODING -> ToneTarget(0.42f, styleWarmth, 0.28f)
            MusicTask.RELAX -> ToneTarget(0.32f, 0.8f, 0.14f)
            MusicTask.WORKOUT -> ToneTarget(0.58f, 0.46f, 0.58f)
        }
    }
}
