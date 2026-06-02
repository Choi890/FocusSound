package com.focussound.music.generation

import com.focussound.composition.Chord
import com.focussound.composition.CompositionPatch
import com.focussound.composition.NoteEvent
import com.focussound.music.form.MusicSection
import com.focussound.music.knowledge.TaskMusicProfileRepository
import com.focussound.music.learning.TaskMusicGrammarLearner
import com.focussound.music.model.GeneratedPiece
import com.focussound.music.model.GeneratedSection
import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicTask
import kotlin.math.roundToInt
import kotlin.random.Random

class TaskAwareComposer(
    private val profileRepository: TaskMusicProfileRepository = TaskMusicProfileRepository(),
    private val grammarLearner: TaskMusicGrammarLearner = TaskMusicGrammarLearner(profileRepository),
    private val formSampler: FormSampler = FormSampler(),
    private val harmonySampler: HarmonySampler = HarmonySampler(),
    private val motifSampler: MotifSampler = MotifSampler(),
    private val arrangementSampler: ArrangementSampler = ArrangementSampler(),
    private val transitionSampler: SectionTransitionSampler = SectionTransitionSampler(),
    private val variationEngine: VariationEngine = VariationEngine(),
    private val noveltyGuard: NoveltyGuard = NoveltyGuard()
) {
    private val recentFingerprints = ArrayDeque<MusicFingerprint>()

    fun generatePatch(request: LiveCompositionRequest): CompositionPatch {
        val piece = generatePiece(request)
        val fingerprint = MusicFingerprint.from(piece)
        remember(fingerprint)
        return piece.toCompositionPatch(buildName(request, fingerprint))
    }

    fun generatePiece(request: LiveCompositionRequest): GeneratedPiece {
        val profile = profileRepository.get(request.task)
        val model = grammarLearner.learn(request.task, request.style)
        val baseTemperature = GenerationTemperature.fromDiversity(request.task, request.diversity)
        repeat(MAX_ATTEMPTS) { attempt ->
            val random = Random(System.nanoTime() xor request.hashCode().toLong() xor (attempt * 7919L))
            val temperature = baseTemperature.boosted(attempt)
            val piece = buildPiece(request, profile, model, temperature, random)
            val fingerprint = MusicFingerprint.from(piece)
            if (!noveltyGuard.isTooSimilar(fingerprint, recentFingerprints.toList())) {
                return piece
            }
        }
        val fallbackRandom = Random(System.nanoTime() xor 0x51AFE5L)
        return buildPiece(request, profile, model, baseTemperature.boosted(MAX_ATTEMPTS), fallbackRandom)
    }

    fun recentFingerprints(): List<MusicFingerprint> = recentFingerprints.toList()

    private fun buildPiece(
        request: LiveCompositionRequest,
        profile: com.focussound.music.knowledge.TaskMusicProfile,
        model: com.focussound.music.learning.LearnedTaskStyleModel,
        temperature: GenerationTemperature,
        random: Random
    ): GeneratedPiece {
        val form = formSampler.sample(profile, model.formModel, temperature, random)
        val key = harmonySampler.sampleKey(request, random)
        val tempo = chooseTempo(profile.tempoRange, request.diversity, random)
        val motif = motifSampler.sample(request.task, model.motifModel, temperature, random)
        val allChords = mutableListOf<Chord>()
        val allNotes = mutableListOf<NoteEvent>()
        val sections = mutableListOf<GeneratedSection>()
        var startBar = 0
        var previousChords = emptyList<Chord>()

        form.sections.forEachIndexed { index, section ->
            val startBeat = startBar * BEATS_PER_BAR
            val sectionChords = harmonySampler.sampleSection(
                request = request,
                profile = profile,
                model = model.harmonyModel,
                key = key,
                section = section,
                temperature = temperature,
                random = random,
                previous = previousChords
            ).let { variationEngine.varyChordOrder(it, temperature, random) }
            val notes = arrangementSampler.sample(
                request = request,
                profile = profile,
                key = key,
                section = section,
                sectionIndex = index,
                chords = sectionChords,
                motif = motif,
                startBeat = startBeat,
                temperature = temperature,
                random = random
            )
            val transition = if (index > 0) {
                transitionSampler.sample(
                    startBeat = startBeat,
                    energy = section.energy,
                    model = model.transitionModel,
                    temperature = temperature,
                    random = random
                )
            } else {
                emptyList()
            }
            val sectionNotes = notes + transition
            allChords += sectionChords
            allNotes += sectionNotes
            sections += GeneratedSection(
                section = section,
                startBar = startBar,
                chords = sectionChords,
                notes = sectionNotes
            )
            previousChords = sectionChords
            startBar += section.bars
        }

        return GeneratedPiece(
            task = request.task,
            style = request.style,
            form = form,
            key = key,
            tempoBpm = tempo,
            chords = allChords,
            notes = allNotes,
            sections = sections,
            fatigueScore = estimateFatigue(request, profile, allNotes),
            durationMinutes = request.targetDurationMinutes ?: defaultDuration(request.task),
            instrumentNames = request.selectedInstruments.map { it.name }
        )
    }

    private fun chooseTempo(range: IntRange, diversity: Float, random: Random): Int {
        val center = ((range.first + range.last) / 2f).roundToInt()
        val spread = ((range.last - range.first) * diversity.coerceIn(0.12f, 1f)).roundToInt().coerceAtLeast(2)
        return random.nextInt(center - spread / 2, center + spread / 2 + 1).coerceIn(range.first, range.last)
    }

    private fun estimateFatigue(
        request: LiveCompositionRequest,
        profile: com.focussound.music.knowledge.TaskMusicProfile,
        notes: List<NoteEvent>
    ): Int {
        val base = when (request.task) {
            MusicTask.SLEEP -> 16
            MusicTask.READING -> 22
            MusicTask.STUDY -> 30
            MusicTask.RELAX -> 28
            MusicTask.CODING -> 36
            MusicTask.WORKOUT -> 56
        }
        val highNotes = notes.count { it.midiNote >= 76 }
        val notePressure = (notes.size / 90f).coerceAtMost(18f)
        val taskPenalty = if (profile.avoidRules.isNotEmpty()) highNotes * 0.12f else 0f
        return (base + request.melodyAmount * 15f + request.rhythmAmount * 20f + notePressure + taskPenalty)
            .roundToInt()
            .coerceIn(5, 100)
    }

    private fun defaultDuration(task: MusicTask): Int = when (task) {
        MusicTask.SLEEP -> 60
        MusicTask.READING -> 30
        MusicTask.WORKOUT -> 25
        else -> 50
    }

    private fun remember(fingerprint: MusicFingerprint) {
        recentFingerprints += fingerprint
        while (recentFingerprints.size > RECENT_MEMORY_LIMIT) {
            recentFingerprints.removeFirst()
        }
    }

    private fun buildName(request: LiveCompositionRequest, fingerprint: MusicFingerprint): String {
        val suffix = kotlin.math.abs(fingerprint.hashCode()).toString().takeLast(3).padStart(3, '0')
        return "${request.task.label} · ${request.style.label} $suffix"
    }

    private companion object {
        const val BEATS_PER_BAR = 4f
        const val MAX_ATTEMPTS = 12
        const val RECENT_MEMORY_LIMIT = 24
    }
}
