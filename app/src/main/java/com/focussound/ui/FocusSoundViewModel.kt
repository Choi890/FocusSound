package com.focussound.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focussound.ai.LocalCompositionPromptParser
import com.focussound.ai.FatigueTarget
import com.focussound.ai.PromptParserFallback
import com.focussound.ai.RuleBasedSoundPromptParser
import com.focussound.audio.AudioSampleBus
import com.focussound.audio.tone.ToneControlState
import com.focussound.condition.ConditionRepository
import com.focussound.condition.FatigueLevel
import com.focussound.condition.MoodLevel
import com.focussound.condition.TimeOfDay
import com.focussound.condition.UserCondition
import com.focussound.composition.CompositionGenre
import com.focussound.composition.CompositionExporter
import com.focussound.composition.CompositionIntent
import com.focussound.composition.CompositionMood
import com.focussound.composition.LocalComposerEngine
import com.focussound.composition.CompositionPatch
import com.focussound.composition.CompositionSetup
import com.focussound.composition.CompositionStyle
import com.focussound.composition.FocusIntensity
import com.focussound.composition.NoteLane
import com.focussound.data.FocusMode
import com.focussound.data.FocusSession
import com.focussound.data.RecommendedSound
import com.focussound.data.SoundType
import com.focussound.data.UserPreference
import com.focussound.data.UserSoundPreset
import com.focussound.data.WeeklyReport
import com.focussound.database.FocusSoundDatabase
import com.focussound.instrument.AutoInstrumentSelector
import com.focussound.instrument.InstrumentImporter
import com.focussound.instrument.InstrumentPolicy
import com.focussound.instrument.InstrumentPreset
import com.focussound.instrument.InstrumentRepository
import com.focussound.instrument.InstrumentSet
import com.focussound.instrument.InstrumentSourceType
import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask
import com.focussound.music.realtime.RollingComposer
import com.focussound.personalization.TasteVectorRepository
import com.focussound.personalization.TasteVectorUpdater
import com.focussound.personalization.UserSoundTasteVector
import com.focussound.recommendation.FatigueEstimator
import com.focussound.recommendation.SoundRecommender
import com.focussound.repository.CompositionPatchRepository
import com.focussound.repository.PresetRepository
import com.focussound.repository.SessionRepository
import com.focussound.repository.UserPreferenceRepository
import com.focussound.service.FocusPlaybackState
import com.focussound.service.FocusSoundController
import com.focussound.service.PlaybackStatus
import com.focussound.sounddesign.SoundPatch
import com.focussound.sounddesign.SoundPatchGenerator
import com.focussound.sounddesign.SoundPatchRepository
import com.focussound.sounddesign.toSoundPatch
import com.focussound.timer.FocusTimer
import com.focussound.playback.PlaybackMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.random.Random

enum class AppScreen {
    HOME,
    AI_DESIGNER,
    AI_COMPOSER,
    CONDITION_CHECK,
    TASTE_PROFILE,
    SOUND_DESIGN,
    PLAYER,
    WEEKLY_REPORT,
    INSTRUMENT_PACK,
    INSTRUMENT_SELECTION,
    MOOD_STYLE_SELECTION,
    SOUND_TYPE_SELECTION,
    COMPOSITION_RESULT
}

enum class TimerPreset {
    TWENTY_FIVE,
    FIFTY,
    CUSTOM
}

enum class RecommendedInstrumentSet(val label: String) {
    PIANO_STRINGS_BASS("피아노 + 현악 + 베이스"),
    PIANO_SOLO("피아노 솔로"),
    STRING_PAD("현악 패드"),
    PAD_BASS("패드 + 베이스"),
    FELT_PIANO_CELLO("펠트 피아노 + 첼로")
}

data class FocusSoundUiState(
    val screen: AppScreen = AppScreen.HOME,
    val preference: UserPreference = UserPreference(),
    val todayRecommendation: RecommendedSound = RecommendedSound(
        profile = UserPreference().toSoundProfile(),
        title = "오늘 추천 사운드",
        reason = "현재 로컬 설정을 기준으로 생성",
        fatigueScore = FatigueEstimator().estimate(UserPreference().toSoundProfile(), 25)
    ),
    val selectedPatch: SoundPatch? = null,
    val generatedPatch: SoundPatch? = null,
    val aiPrompt: String = "",
    val compositionPrompt: String = "",
    val generatedComposition: CompositionPatch? = null,
    val selectedComposition: CompositionPatch? = null,
    val recentPresets: List<UserSoundPreset> = emptyList(),
    val recentPatches: List<SoundPatch> = emptyList(),
    val recentSessions: List<FocusSession> = emptyList(),
    val tasteVectors: List<UserSoundTasteVector> = emptyList(),
    val instrumentPresets: List<InstrumentPreset> = emptyList(),
    val selectedMusicTask: MusicTask = MusicTask.STUDY,
    val selectedMusicStyle: MusicStyle = MusicStyle.RELAXING_PIANO,
    val liveMelodyAmount: Float = 0.28f,
    val liveRhythmAmount: Float = 0.08f,
    val liveDiversity: Float = 0.46f,
    val compositionSetup: CompositionSetup = CompositionSetup(),
    val selectedInstrumentSet: InstrumentSet? = null,
    val playbackMode: PlaybackMode = PlaybackMode.SOUND_DESIGN,
    val toneBrightness: Float = 0.5f,
    val toneWarmth: Float = 0.5f,
    val toneColdness: Float = 0.2f,
    val condition: UserCondition = UserCondition(),
    val conditionSnapshotId: Long? = null,
    val weeklyReport: WeeklyReport = WeeklyReport(),
    val playbackState: FocusPlaybackState = FocusPlaybackState(),
    val waveformSamples: FloatArray = FloatArray(AudioSampleBus.WAVEFORM_SIZE),
    val fatigueScore: Int = FatigueEstimator().estimate(UserPreference().toSoundProfile(), 25),
    val timerPreset: TimerPreset = TimerPreset.TWENTY_FIVE,
    val durationMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val elapsedSeconds: Int = 0,
    val focusRating: Int = 4,
    val fatigueRating: Int = 2,
    val tooBright: Boolean = false,
    val tooMuffled: Boolean = false,
    val tooMuchBass: Boolean = false,
    val melodyAnnoying: Boolean = false,
    val rhythmAnnoying: Boolean = false,
    val harmonyLiked: Boolean = false,
    val tooRepetitive: Boolean = false,
    val focusedWell: Boolean = false,
    val tooDark: Boolean = false,
    val useAgain: Boolean = false,
    val showExportDialog: Boolean = false,
    val exportMessage: String? = null,
    val instrumentMessage: String? = null,
    val errorMessage: String? = null
) {
    val isRunning: Boolean = playbackState.status != PlaybackStatus.STOPPED
    val isPlaying: Boolean = playbackState.status == PlaybackStatus.PLAYING
    val activePatch: SoundPatch
        get() = selectedPatch ?: preference.toSoundProfile().toSoundPatch(durationMinutes)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FocusSoundUiState) return false
        return screen == other.screen &&
            preference == other.preference &&
            todayRecommendation == other.todayRecommendation &&
            selectedPatch == other.selectedPatch &&
            generatedPatch == other.generatedPatch &&
            aiPrompt == other.aiPrompt &&
            compositionPrompt == other.compositionPrompt &&
            generatedComposition == other.generatedComposition &&
            selectedComposition == other.selectedComposition &&
            recentPresets == other.recentPresets &&
            recentPatches == other.recentPatches &&
            recentSessions == other.recentSessions &&
            tasteVectors == other.tasteVectors &&
            instrumentPresets == other.instrumentPresets &&
            selectedMusicTask == other.selectedMusicTask &&
            selectedMusicStyle == other.selectedMusicStyle &&
            liveMelodyAmount == other.liveMelodyAmount &&
            liveRhythmAmount == other.liveRhythmAmount &&
            liveDiversity == other.liveDiversity &&
            compositionSetup == other.compositionSetup &&
            selectedInstrumentSet == other.selectedInstrumentSet &&
            playbackMode == other.playbackMode &&
            toneBrightness == other.toneBrightness &&
            toneWarmth == other.toneWarmth &&
            toneColdness == other.toneColdness &&
            condition == other.condition &&
            conditionSnapshotId == other.conditionSnapshotId &&
            weeklyReport == other.weeklyReport &&
            playbackState == other.playbackState &&
            waveformSamples.contentEquals(other.waveformSamples) &&
            fatigueScore == other.fatigueScore &&
            timerPreset == other.timerPreset &&
            durationMinutes == other.durationMinutes &&
            remainingSeconds == other.remainingSeconds &&
            elapsedSeconds == other.elapsedSeconds &&
            focusRating == other.focusRating &&
            fatigueRating == other.fatigueRating &&
            tooBright == other.tooBright &&
            tooMuffled == other.tooMuffled &&
            tooMuchBass == other.tooMuchBass &&
            melodyAnnoying == other.melodyAnnoying &&
            rhythmAnnoying == other.rhythmAnnoying &&
            harmonyLiked == other.harmonyLiked &&
            tooRepetitive == other.tooRepetitive &&
            focusedWell == other.focusedWell &&
            tooDark == other.tooDark &&
            useAgain == other.useAgain &&
            showExportDialog == other.showExportDialog &&
            exportMessage == other.exportMessage &&
            instrumentMessage == other.instrumentMessage &&
            errorMessage == other.errorMessage
    }

    override fun hashCode(): Int {
        var result = screen.hashCode()
        result = 31 * result + preference.hashCode()
        result = 31 * result + todayRecommendation.hashCode()
        result = 31 * result + (selectedPatch?.hashCode() ?: 0)
        result = 31 * result + (generatedPatch?.hashCode() ?: 0)
        result = 31 * result + aiPrompt.hashCode()
        result = 31 * result + compositionPrompt.hashCode()
        result = 31 * result + (generatedComposition?.hashCode() ?: 0)
        result = 31 * result + (selectedComposition?.hashCode() ?: 0)
        result = 31 * result + recentPresets.hashCode()
        result = 31 * result + recentPatches.hashCode()
        result = 31 * result + recentSessions.hashCode()
        result = 31 * result + tasteVectors.hashCode()
        result = 31 * result + instrumentPresets.hashCode()
        result = 31 * result + selectedMusicTask.hashCode()
        result = 31 * result + selectedMusicStyle.hashCode()
        result = 31 * result + liveMelodyAmount.hashCode()
        result = 31 * result + liveRhythmAmount.hashCode()
        result = 31 * result + liveDiversity.hashCode()
        result = 31 * result + compositionSetup.hashCode()
        result = 31 * result + (selectedInstrumentSet?.hashCode() ?: 0)
        result = 31 * result + playbackMode.hashCode()
        result = 31 * result + toneBrightness.hashCode()
        result = 31 * result + toneWarmth.hashCode()
        result = 31 * result + toneColdness.hashCode()
        result = 31 * result + condition.hashCode()
        result = 31 * result + (conditionSnapshotId?.hashCode() ?: 0)
        result = 31 * result + weeklyReport.hashCode()
        result = 31 * result + playbackState.hashCode()
        result = 31 * result + waveformSamples.contentHashCode()
        result = 31 * result + fatigueScore
        result = 31 * result + timerPreset.hashCode()
        result = 31 * result + durationMinutes
        result = 31 * result + remainingSeconds
        result = 31 * result + elapsedSeconds
        result = 31 * result + focusRating
        result = 31 * result + fatigueRating
        result = 31 * result + tooBright.hashCode()
        result = 31 * result + tooMuffled.hashCode()
        result = 31 * result + tooMuchBass.hashCode()
        result = 31 * result + melodyAnnoying.hashCode()
        result = 31 * result + rhythmAnnoying.hashCode()
        result = 31 * result + harmonyLiked.hashCode()
        result = 31 * result + tooRepetitive.hashCode()
        result = 31 * result + focusedWell.hashCode()
        result = 31 * result + tooDark.hashCode()
        result = 31 * result + useAgain.hashCode()
        result = 31 * result + showExportDialog.hashCode()
        result = 31 * result + (exportMessage?.hashCode() ?: 0)
        result = 31 * result + (instrumentMessage?.hashCode() ?: 0)
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        return result
    }
}

class FocusSoundViewModel(application: Application) : AndroidViewModel(application) {
    private val database = FocusSoundDatabase.getInstance(application)
    private val preferenceRepository = UserPreferenceRepository(application)
    private val presetRepository = PresetRepository(database.presetDao())
    private val sessionRepository = SessionRepository(database.focusSessionDao())
    private val soundPatchRepository = SoundPatchRepository(database.soundPatchDao())
    private val compositionRepository = CompositionPatchRepository(database.compositionDao())
    private val instrumentRepository = InstrumentRepository(application, database.instrumentDao())
    private val tasteVectorRepository = TasteVectorRepository(database.userTasteVectorDao())
    private val conditionRepository = ConditionRepository(application, database.conditionSnapshotDao())
    private val recommender = SoundRecommender()
    private val fatigueEstimator = FatigueEstimator()
    private val parser = PromptParserFallback(null, RuleBasedSoundPromptParser())
    private val patchGenerator = SoundPatchGenerator()
    private val compositionParser = LocalCompositionPromptParser()
    private val compositionGenerator = LocalComposerEngine()
    private val compositionExporter = CompositionExporter()
    private val rollingComposer = RollingComposer()
    private val instrumentSelector = AutoInstrumentSelector()
    private val instrumentImporter = InstrumentImporter()
    private val tasteVectorUpdater = TasteVectorUpdater()
    private val focusTimer = FocusTimer(viewModelScope)

    private val _uiState = MutableStateFlow(FocusSoundUiState())
    val uiState: StateFlow<FocusSoundUiState> = _uiState.asStateFlow()

    private var activePatch: SoundPatch? = null
    private var activeCompositionPatch: CompositionPatch? = null
    private var activeInstrumentSet: InstrumentSet? = null
    private var activePlaybackMode: PlaybackMode = PlaybackMode.SOUND_DESIGN
    private var activeStartedAtMillis: Long = 0L
    private var activeConditionSnapshotId: Long? = null
    private var pendingSession: FocusSession? = null
    private var finishingSession = false

    init {
        combine(
            preferenceRepository.preferenceFlow,
            sessionRepository.sessions,
            presetRepository.recentPresets,
            sessionRepository.weeklyReport
        ) { preference, sessions, presets, weeklyReport ->
            StoredState(preference, sessions, presets, weeklyReport)
        }.onEach(::mergeStoredState)
            .launchIn(viewModelScope)

        conditionRepository.condition
            .onEach { condition -> _uiState.update { it.copy(condition = condition) } }
            .launchIn(viewModelScope)

        tasteVectorRepository.tasteVectors
            .onEach { vectors -> _uiState.update { it.copy(tasteVectors = vectors) } }
            .launchIn(viewModelScope)

        soundPatchRepository.recentPatches
            .onEach { patches -> _uiState.update { it.copy(recentPatches = patches) } }
            .launchIn(viewModelScope)

        instrumentRepository.presets
            .onEach { presets ->
                _uiState.update { state ->
                    val selectedInstruments = state.compositionSetup.selectedInstruments.ifEmpty {
                        presets.takeDefaultSetupInstruments()
                    }
                    state.copy(
                        instrumentPresets = presets,
                        compositionSetup = state.compositionSetup.copy(
                            selectedInstruments = selectedInstruments
                        )
                    )
                }
            }
            .launchIn(viewModelScope)

        FocusSoundController.playbackState
            .onEach(::mergePlaybackState)
            .launchIn(viewModelScope)

        AudioSampleBus.samples
            .onEach { samples -> _uiState.update { it.copy(waveformSamples = samples) } }
            .launchIn(viewModelScope)
    }

    fun selectMode(mode: FocusMode) {
        updatePreference { it.copy(mode = mode) }
        _uiState.update {
            it.copy(
                compositionSetup = it.compositionSetup.copy(
                    mode = mode,
                    style = mode.defaultCompositionStyle()
                )
            )
        }
    }

    fun selectSoundType(soundType: SoundType) {
        updatePreference { it.copy(soundType = soundType) }
        _uiState.update {
            it.copy(compositionSetup = it.compositionSetup.copy(soundType = soundType))
        }
    }

    fun selectMusicTask(task: MusicTask) {
        val mode = task.toFocusMode()
        val style = task.defaultMusicStyle()
        updatePreference {
            it.copy(
                mode = mode,
                soundType = if (task == MusicTask.STUDY || task == MusicTask.SLEEP) SoundType.NONE else it.soundType
            )
        }
        _uiState.update {
            it.copy(
                selectedMusicTask = task,
                selectedMusicStyle = style,
                compositionSetup = it.compositionSetup.copy(
                    selectedInstruments = it.instrumentPresets.takeDefaultLiveInstruments(task),
                    mode = mode,
                    style = style.toCompositionStyle(),
                    soundType = if (task == MusicTask.STUDY || task == MusicTask.SLEEP) SoundType.NONE else it.preference.soundType
                ),
                liveMelodyAmount = task.defaultMelodyAmount(),
                liveRhythmAmount = task.defaultRhythmAmount(),
                liveDiversity = task.defaultDiversity()
            )
        }
    }

    fun selectMusicStyle(style: MusicStyle) {
        _uiState.update {
            it.copy(
                selectedMusicStyle = style,
                compositionSetup = it.compositionSetup.copy(style = style.toCompositionStyle())
            )
        }
    }

    fun updateLiveMelodyAmount(value: Float) {
        _uiState.update { it.copy(liveMelodyAmount = value.coerceIn(0f, 1f)) }
    }

    fun updateLiveRhythmAmount(value: Float) {
        _uiState.update { it.copy(liveRhythmAmount = value.coerceIn(0f, 1f)) }
    }

    fun updateLiveDiversity(value: Float) {
        _uiState.update { it.copy(liveDiversity = value.coerceIn(0f, 1f)) }
    }
    fun updateBrightness(value: Float) = updatePreference { it.copy(brightness = value.coerceIn(0f, 1f)) }
    fun updateWarmth(value: Float) = updatePreference { it.copy(warmth = value.coerceIn(0f, 1f)) }
    fun updateMovement(value: Float) = updatePreference { it.copy(movement = value.coerceIn(0f, 1f)) }

    fun updateToneBrightness(value: Float) = updateTone { it.copy(toneBrightness = value.coerceIn(0f, 1f)) }
    fun updateToneWarmth(value: Float) = updateTone { it.copy(toneWarmth = value.coerceIn(0f, 1f)) }
    fun updateToneColdness(value: Float) = updateTone { it.copy(toneColdness = value.coerceIn(0f, 1f)) }

    fun selectTimerPreset(preset: TimerPreset) {
        if (_uiState.value.isRunning) return
        val current = _uiState.value.preference
        val minutes = when (preset) {
            TimerPreset.TWENTY_FIVE -> 25
            TimerPreset.FIFTY -> 50
            TimerPreset.CUSTOM -> current.customTimerMinutes
        }
        updatePreference { it.copy(timerMinutes = minutes) }
    }

    fun updateCustomTimerMinutes(minutes: Int) {
        if (_uiState.value.isRunning) return
        val adjusted = minutes.coerceIn(5, 180)
        updatePreference { it.copy(timerMinutes = adjusted, customTimerMinutes = adjusted) }
    }

    fun updateAiPrompt(prompt: String) {
        _uiState.update { it.copy(aiPrompt = prompt) }
    }

    fun updateCompositionPrompt(prompt: String) {
        _uiState.update { it.copy(compositionPrompt = prompt) }
    }

    fun generatePatchFromPrompt() {
        val prompt = _uiState.value.aiPrompt.ifBlank {
            "부드러운 코딩 집중 사운드, 고역은 줄이고 따뜻하게"
        }
        viewModelScope.launch {
            val state = _uiState.value
            val intent = parser.parse(prompt)
            val taste = state.tasteVectors.firstOrNull { it.mode == intent.mode }
            val patch = patchGenerator.generate(intent, taste, state.condition)
            soundPatchRepository.save(patch)
            applyPatchToState(patch, screen = AppScreen.AI_DESIGNER)
        }
    }

    fun generateCompositionFromPrompt() {
        val prompt = _uiState.value.compositionPrompt.ifBlank {
            "새벽 코딩용, 따뜻한 패드 중심, 멜로디는 적게"
        }
        viewModelScope.launch {
            val state = _uiState.value
            val intent = compositionParser.parse(prompt)
            val taste = state.tasteVectors.firstOrNull { it.mode == intent.mode }
            val instrumentSet = instrumentSelector.selectFor(
                intent = intent,
                available = state.instrumentPresets.ifEmpty { instrumentRepository.builtInPresets },
                policy = InstrumentPolicy.AUTO_REAL_SAMPLES
            )
            val composition = compositionGenerator.compose(intent, taste, state.condition)
                .copy(instrumentNames = instrumentSet.names)
            val backdropPatch = composition.toBackdropPatch(state.preference, PlaybackMode.AI_COMPOSITION_ONLY)
            compositionRepository.save(composition)
            soundPatchRepository.save(backdropPatch)
            applyCompositionToState(
                composition = composition,
                patch = backdropPatch,
                instrumentSet = instrumentSet,
                playbackMode = PlaybackMode.AI_COMPOSITION_ONLY,
                screen = AppScreen.AI_COMPOSER
            )
        }
    }

    fun generateCompositionFromSetup() {
        viewModelScope.launch {
            val state = _uiState.value
            val setup = state.compositionSetup
            val instruments = setup.selectedInstruments.ifEmpty {
                state.instrumentPresets.takeDefaultSetupInstruments()
            }
            val instrumentSet = instruments.toInstrumentSet()
            val intent = setup.toCompositionIntent(state.preference.timerMinutes)
            val taste = state.tasteVectors.firstOrNull { it.mode == intent.mode }
            val playbackMode = if (setup.soundType == SoundType.NONE) {
                PlaybackMode.AI_COMPOSITION_ONLY
            } else {
                PlaybackMode.AI_COMPOSITION_WITH_TEXTURE
            }
            val composition = compositionGenerator.compose(intent, taste, state.condition)
                .copy(instrumentNames = instrumentSet.names)
            val patch = composition.toBackdropPatch(
                preference = state.preference.copy(soundType = setup.soundType),
                playbackMode = playbackMode
            )
            compositionRepository.save(composition)
            soundPatchRepository.save(patch)
            applyCompositionToState(
                composition = composition,
                patch = patch,
                instrumentSet = instrumentSet,
                playbackMode = playbackMode,
                screen = AppScreen.COMPOSITION_RESULT
            )
        }
    }

    fun generateHomeCompositionAndPlay() {
        if (_uiState.value.isRunning) return

        viewModelScope.launch {
            val state = _uiState.value
            val availableInstruments = state.instrumentPresets.ifEmpty { instrumentRepository.builtInPresets }
            val selectedInstruments = state.compositionSetup.selectedInstruments.ifEmpty {
                availableInstruments.takeDefaultLiveInstruments(state.selectedMusicTask)
            }
            val instrumentSet = selectedInstruments.toInstrumentSet()
            val request = LiveCompositionRequest(
                task = state.selectedMusicTask,
                selectedInstruments = selectedInstruments,
                style = state.selectedMusicStyle,
                soundType = state.preference.soundType,
                diversity = state.liveDiversity,
                melodyAmount = state.liveMelodyAmount,
                rhythmAmount = state.liveRhythmAmount,
                targetDurationMinutes = state.preference.timerMinutes
            )
            val playbackMode = if (request.soundType == SoundType.NONE) {
                PlaybackMode.AI_COMPOSITION_ONLY
            } else {
                PlaybackMode.AI_COMPOSITION_WITH_TEXTURE
            }
            val composition = rollingComposer.compose(request).copy(instrumentNames = instrumentSet.names)
            val patch = composition.toBackdropPatch(
                preference = state.preference.copy(
                    mode = request.focusMode,
                    soundType = request.soundType
                ),
                playbackMode = playbackMode
            )
            compositionRepository.save(composition)
            soundPatchRepository.save(patch)
            _uiState.update {
                it.copy(
                    compositionSetup = it.compositionSetup.copy(
                        selectedInstruments = selectedInstruments,
                        mode = request.focusMode,
                        style = request.style.toCompositionStyle(),
                        soundType = request.soundType
                    )
                )
            }
            applyCompositionToState(
                composition = composition,
                patch = patch,
                instrumentSet = instrumentSet,
                playbackMode = playbackMode,
                screen = AppScreen.HOME
            )
            startFocusSession(openPlayer = false)
        }
    }

    fun startAiCompositionOnlyNow() {
        val quickPrompt = when (_uiState.value.preference.mode) {
            FocusMode.CODING -> "새벽 코딩용, 따뜻한 패드, 멜로디 적게"
            FocusMode.STUDY -> "공부용, 부드러운 피아노와 패드, 리듬 거의 없음"
            FocusMode.READING -> "독서용, 현악 패드 중심, 멜로디 없음"
            FocusMode.SLEEP -> "수면용, 어두운 패드, 리듬 없음"
        }
        _uiState.update { it.copy(compositionPrompt = quickPrompt) }
        viewModelScope.launch {
            val state = _uiState.value
            val intent = compositionParser.parse(quickPrompt).copy(mode = state.preference.mode)
            val taste = state.tasteVectors.firstOrNull { it.mode == intent.mode }
            val instrumentSet = instrumentSelector.selectFor(
                intent = intent,
                available = state.instrumentPresets.ifEmpty { instrumentRepository.builtInPresets },
                policy = InstrumentPolicy.AUTO_REAL_SAMPLES
            )
            val composition = compositionGenerator.compose(intent, taste, state.condition)
                .copy(instrumentNames = instrumentSet.names)
            val patch = composition.toBackdropPatch(state.preference, PlaybackMode.AI_COMPOSITION_ONLY)
            compositionRepository.save(composition)
            soundPatchRepository.save(patch)
            applyCompositionToState(composition, patch, instrumentSet, PlaybackMode.AI_COMPOSITION_ONLY, AppScreen.AI_COMPOSER)
            startFocusSession()
        }
    }

    fun selectPatch(patch: SoundPatch) {
        viewModelScope.launch { soundPatchRepository.markUsed(patch.id) }
        applyPatchToState(patch, screen = _uiState.value.screen)
    }

    fun makeGeneratedWarmer() = adjustSelectedPatch(warmthDelta = 0.08f, brightnessDelta = -0.04f)
    fun reduceGeneratedRain() = adjustSelectedPatch(rainDelta = -0.15f)
    fun reduceGeneratedMovement() = adjustSelectedPatch(movementDelta = -0.08f)

    fun makeCompositionWarmer() = adjustSelectedComposition(
        warmthDelta = 0.08f,
        harmonicDelta = -0.03f
    )

    fun reduceCompositionMelody() = adjustSelectedComposition(
        melodyMultiplier = 0.58f,
        fatigueDelta = -6
    )

    fun reduceCompositionRhythm() = adjustSelectedComposition(
        rhythmMultiplier = 0.45f,
        fatigueDelta = -5
    )

    fun makeCompositionOrchestral() = adjustSelectedComposition(
        genre = CompositionGenre.ORCHESTRAL_PAD,
        harmonicDelta = 0.08f,
        melodyMultiplier = 0.82f,
        rhythmMultiplier = 0.58f,
        fatigueDelta = 2
    )

    fun makeCompositionDarker() = adjustSelectedComposition(
        brightnessDelta = -0.08f,
        harmonicDelta = -0.02f,
        fatigueDelta = -3
    )

    fun increaseCompositionPad() = adjustSelectedComposition(
        padDelta = 0.14f,
        fatigueDelta = -2
    )

    fun makeCompositionSleepReady() = adjustSelectedComposition(
        genre = CompositionGenre.SLEEP_DRONE,
        mode = FocusMode.SLEEP,
        melodyMultiplier = 0.28f,
        rhythmMultiplier = 0.2f,
        harmonicDelta = -0.08f,
        fatigueDelta = -12,
        durationMinutes = 45
    )

    fun applyRecommendation() {
        val profile = _uiState.value.todayRecommendation.profile
        val patch = profile.toSoundPatch(_uiState.value.durationMinutes).copy(name = "오늘 추천 사운드")
        applyPatchToState(patch, screen = _uiState.value.screen)
    }

    fun applyPreset(preset: UserSoundPreset) {
        val patch = preset.profile.toSoundPatch(_uiState.value.durationMinutes).copy(name = preset.name)
        applyPatchToState(patch, screen = _uiState.value.screen)
        viewModelScope.launch { presetRepository.markUsed(preset.id) }
    }

    fun saveCurrentPreset(name: String) {
        val profile = _uiState.value.activePatch.toSoundProfile()
        viewModelScope.launch { presetRepository.savePreset(name, profile) }
    }

    fun updateCondition(
        sleepMinutes: Int?,
        fatigue: FatigueLevel,
        mood: MoodLevel,
        timeOfDay: TimeOfDay
    ) {
        viewModelScope.launch {
            val snapshotId = conditionRepository.updateManualCondition(sleepMinutes, fatigue, mood, timeOfDay)
            _uiState.update { it.copy(conditionSnapshotId = snapshotId) }
        }
    }

    fun goHome() {
        if (_uiState.value.isRunning) {
            _uiState.update { it.copy(screen = AppScreen.PLAYER) }
        } else {
            pendingSession = null
            resetFeedback()
            _uiState.update {
                it.copy(
                    screen = AppScreen.HOME,
                    remainingSeconds = it.durationMinutes * 60,
                    elapsedSeconds = 0,
                    errorMessage = null
                )
            }
        }
    }

    fun goToAiDesigner() = _uiState.update { it.copy(screen = AppScreen.AI_DESIGNER, errorMessage = null) }
    fun goToAiComposer() = _uiState.update { it.copy(screen = AppScreen.AI_COMPOSER, errorMessage = null) }
    fun startCompositionSetup() = _uiState.update {
        it.copy(
            screen = AppScreen.INSTRUMENT_SELECTION,
            compositionSetup = it.compositionSetup.copy(
                selectedInstruments = it.compositionSetup.selectedInstruments.ifEmpty {
                    it.instrumentPresets.takeDefaultSetupInstruments()
                },
                mode = it.preference.mode
            ),
            errorMessage = null
        )
    }
    fun goToMoodStyleSelection() = _uiState.update { it.copy(screen = AppScreen.MOOD_STYLE_SELECTION, errorMessage = null) }
    fun goToSoundTypeSelection() = _uiState.update { it.copy(screen = AppScreen.SOUND_TYPE_SELECTION, errorMessage = null) }
    fun goToCompositionResult() = _uiState.update { it.copy(screen = AppScreen.COMPOSITION_RESULT, errorMessage = null) }
    fun goToInstrumentPack() = _uiState.update { it.copy(screen = AppScreen.INSTRUMENT_PACK, errorMessage = null) }
    fun goToConditionCheck() = _uiState.update { it.copy(screen = AppScreen.CONDITION_CHECK, errorMessage = null) }
    fun goToTasteProfile() = _uiState.update { it.copy(screen = AppScreen.TASTE_PROFILE, errorMessage = null) }
    fun goToSoundDesign() = _uiState.update { it.copy(screen = AppScreen.SOUND_DESIGN, errorMessage = null) }
    fun goToPlayer() = _uiState.update { it.copy(screen = AppScreen.PLAYER, errorMessage = null) }
    fun goToWeeklyReport() = _uiState.update { it.copy(screen = AppScreen.WEEKLY_REPORT, errorMessage = null) }

    fun onBack() {
        when (_uiState.value.screen) {
            AppScreen.HOME -> Unit
            AppScreen.PLAYER -> _uiState.update { it.copy(screen = if (it.isRunning) AppScreen.HOME else AppScreen.SOUND_DESIGN) }
            AppScreen.MOOD_STYLE_SELECTION -> _uiState.update { it.copy(screen = AppScreen.INSTRUMENT_SELECTION) }
            AppScreen.SOUND_TYPE_SELECTION -> _uiState.update { it.copy(screen = AppScreen.MOOD_STYLE_SELECTION) }
            AppScreen.COMPOSITION_RESULT -> _uiState.update { it.copy(screen = AppScreen.SOUND_TYPE_SELECTION) }
            else -> _uiState.update { it.copy(screen = AppScreen.HOME) }
        }
    }

    fun toggleSetupInstrument(preset: InstrumentPreset) {
        _uiState.update { state ->
            val current = state.compositionSetup.selectedInstruments
            val next = if (current.any { it.id == preset.id }) {
                current.filterNot { it.id == preset.id }
            } else {
                (current + preset).take(4)
            }
            state.copy(compositionSetup = state.compositionSetup.copy(selectedInstruments = next))
        }
    }

    fun applyRecommendedInstrumentSet(type: RecommendedInstrumentSet) {
        _uiState.update { state ->
            state.copy(
                compositionSetup = state.compositionSetup.copy(
                    selectedInstruments = type.resolve(state.instrumentPresets)
                )
            )
        }
    }

    fun selectSetupMood(mood: CompositionMood) {
        _uiState.update { it.copy(compositionSetup = it.compositionSetup.copy(mood = mood)) }
    }

    fun selectSetupStyle(style: CompositionStyle) {
        _uiState.update { it.copy(compositionSetup = it.compositionSetup.copy(style = style)) }
    }

    fun selectSetupIntensity(intensity: FocusIntensity) {
        _uiState.update { it.copy(compositionSetup = it.compositionSetup.copy(focusIntensity = intensity)) }
    }

    fun selectSetupSoundType(soundType: SoundType) {
        _uiState.update {
            it.copy(
                compositionSetup = it.compositionSetup.copy(soundType = soundType),
                preference = it.preference.copy(soundType = soundType)
            )
        }
    }

    fun startFocusSession() = startFocusSession(openPlayer = true)

    private fun startFocusSession(openPlayer: Boolean) {
        val state = _uiState.value
        if (state.isRunning) return

        val patch = state.activePatch
        val compositionPatch = state.selectedComposition
        val instrumentSet = state.selectedInstrumentSet
        val playbackMode = state.playbackMode
        val durationSeconds = patch.durationMinutes.coerceIn(1, 240) * 60
        val fatigueScore = compositionPatch?.fatigueScore
            ?: fatigueEstimator.estimate(patch.toSoundProfile(), patch.durationMinutes)

        activePatch = patch
        activeCompositionPatch = compositionPatch
        activeInstrumentSet = instrumentSet
        activePlaybackMode = playbackMode
        activeConditionSnapshotId = state.conditionSnapshotId
        activeStartedAtMillis = System.currentTimeMillis()
        pendingSession = null
        finishingSession = false

        FocusSoundController.playPatch(
            context = getApplication(),
            patch = patch,
            durationMinutes = patch.durationMinutes,
            compositionPatch = compositionPatch,
            instrumentSet = instrumentSet,
            playbackMode = playbackMode
        )
        FocusSoundController.updateTone(
            context = getApplication(),
            tone = ToneControlState(state.toneBrightness, state.toneWarmth, state.toneColdness)
        )
        viewModelScope.launch {
            soundPatchRepository.save(patch)
            compositionPatch?.let {
                compositionRepository.save(it)
                compositionRepository.markUsed(it.id)
            }
        }

        _uiState.update {
            it.copy(
                screen = if (openPlayer) AppScreen.PLAYER else AppScreen.HOME,
                selectedPatch = patch,
                selectedComposition = compositionPatch,
                selectedInstrumentSet = instrumentSet,
                playbackMode = playbackMode,
                fatigueScore = fatigueScore,
                durationMinutes = patch.durationMinutes,
                remainingSeconds = durationSeconds,
                elapsedSeconds = 0,
                errorMessage = null
            )
        }

        focusTimer.start(
            durationSeconds = durationSeconds,
            onTick = { remaining, elapsed ->
                _uiState.update { it.copy(remainingSeconds = remaining, elapsedSeconds = elapsed) }
            },
            onFinish = { finishActiveSession(completed = true, stopService = true) }
        )
    }

    fun pausePlayback() = FocusSoundController.pause(getApplication())
    fun resumePlayback() = FocusSoundController.resume(getApplication())

    fun stopFocusSession() {
        if (!_uiState.value.isRunning && activePatch == null) return
        finishActiveSession(completed = false, stopService = true)
    }

    fun updateFocusRating(rating: Int) = _uiState.update { it.copy(focusRating = rating.coerceIn(1, 5)) }
    fun updateFatigueRating(rating: Int) = _uiState.update { it.copy(fatigueRating = rating.coerceIn(1, 5)) }
    fun setTooBright(value: Boolean) = _uiState.update { it.copy(tooBright = value) }
    fun setTooMuffled(value: Boolean) = _uiState.update { it.copy(tooMuffled = value) }
    fun setTooMuchBass(value: Boolean) = _uiState.update { it.copy(tooMuchBass = value) }
    fun setMelodyAnnoying(value: Boolean) = _uiState.update { it.copy(melodyAnnoying = value) }
    fun setRhythmAnnoying(value: Boolean) = _uiState.update { it.copy(rhythmAnnoying = value) }
    fun setHarmonyLiked(value: Boolean) = _uiState.update { it.copy(harmonyLiked = value) }
    fun setTooRepetitive(value: Boolean) = _uiState.update { it.copy(tooRepetitive = value) }
    fun setFocusedWell(value: Boolean) = _uiState.update { it.copy(focusedWell = value) }
    fun setTooDark(value: Boolean) = _uiState.update { it.copy(tooDark = value) }
    fun setUseAgain(value: Boolean) = _uiState.update { it.copy(useAgain = value) }

    fun showExportDialog() = _uiState.update { it.copy(showExportDialog = true, exportMessage = null) }
    fun hideExportDialog() = _uiState.update { it.copy(showExportDialog = false) }

    fun saveGeneratedComposition() {
        val composition = _uiState.value.selectedComposition ?: _uiState.value.generatedComposition ?: return
        val patch = _uiState.value.activePatch
        viewModelScope.launch {
            compositionRepository.save(composition)
            soundPatchRepository.save(patch)
            _uiState.update { it.copy(exportMessage = "로컬 DB에 저장했습니다.") }
        }
    }

    fun exportCompositionAsWav() {
        exportComposition { composition, directory ->
            compositionExporter.exportWav(composition, directory)
        }
    }

    fun exportCompositionAsMidi() {
        exportComposition { composition, directory ->
            compositionExporter.exportMidi(composition, directory)
        }
    }

    fun importWavInstrument(uri: Uri) {
        viewModelScope.launch {
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    instrumentImporter.importWav(getApplication(), uri)
                }
            }
            result.onSuccess { preset ->
                instrumentRepository.saveImported(preset)
                _uiState.update {
                    it.copy(instrumentMessage = "WAV 악기 '${preset.name}'을 가져왔습니다.")
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(instrumentMessage = "WAV 가져오기 실패: ${error.message ?: "알 수 없는 오류"}")
                }
            }
        }
    }

    fun importSf2Instrument(uri: Uri) {
        viewModelScope.launch {
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    instrumentImporter.importSf2(getApplication(), uri)
                }
            }
            result.onSuccess { preset ->
                instrumentRepository.saveImported(preset)
                _uiState.update {
                    it.copy(instrumentMessage = "SF2 '${preset.name}'을 보관했습니다. 실제 재생 연동은 로컬 신스 확장 작업으로 남겨두었습니다.")
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(instrumentMessage = "SF2 가져오기 실패: ${error.message ?: "알 수 없는 오류"}")
                }
            }
        }
    }

    fun releaseAudio() {
        focusTimer.stop()
        FocusSoundController.stop(getApplication())
        _uiState.update { it.copy(playbackState = FocusPlaybackState()) }
    }

    override fun onCleared() {
        focusTimer.stop()
        super.onCleared()
    }

    private fun updatePreference(transform: (UserPreference) -> UserPreference) {
        val updated = transform(_uiState.value.preference)
        val durationMinutes = updated.timerMinutes.coerceIn(1, 240)
        val fatigueScore = fatigueEstimator.estimate(updated.toSoundProfile(), durationMinutes)

        _uiState.update {
            it.copy(
                preference = updated,
                selectedPatch = null,
                selectedComposition = null,
                selectedInstrumentSet = null,
                playbackMode = PlaybackMode.SOUND_DESIGN,
                timerPreset = presetFor(durationMinutes),
                durationMinutes = durationMinutes,
                remainingSeconds = if (it.isRunning) it.remainingSeconds else durationMinutes * 60,
                fatigueScore = fatigueScore,
                errorMessage = null
            )
        }

        viewModelScope.launch { preferenceRepository.save(updated) }
    }

    private fun updateTone(transform: (FocusSoundUiState) -> FocusSoundUiState) {
        _uiState.update(transform)
        val state = _uiState.value
        val tone = ToneControlState(
            brightness = state.toneBrightness,
            warmth = state.toneWarmth,
            coldness = state.toneColdness
        )
        if (state.isRunning) {
            FocusSoundController.updateTone(getApplication(), tone)
        }
    }

    private fun mergeStoredState(storedState: StoredState) {
        val preference = storedState.preference
        val durationMinutes = preference.timerMinutes.coerceIn(1, 240)
        val recommendation = recommender.recommendToday(
            preference = preference,
            sessions = storedState.sessions,
            presets = storedState.presets
        )
        val fatigueScore = fatigueEstimator.estimate(preference.toSoundProfile(), durationMinutes)

        _uiState.update { current ->
            current.copy(
                preference = preference,
                todayRecommendation = recommendation,
                recentPresets = storedState.presets,
                recentSessions = storedState.sessions.take(5),
                weeklyReport = storedState.weeklyReport,
                fatigueScore = fatigueScore,
                timerPreset = presetFor(durationMinutes),
                durationMinutes = durationMinutes,
                remainingSeconds = if (current.isRunning) current.remainingSeconds else durationMinutes * 60
            )
        }
    }

    private fun mergePlaybackState(playbackState: FocusPlaybackState) {
        val shouldCreateReport = playbackState.status == PlaybackStatus.STOPPED &&
            activePatch != null &&
            _uiState.value.isRunning &&
            !finishingSession

        _uiState.update { it.copy(playbackState = playbackState) }

        if (shouldCreateReport) {
            finishActiveSession(completed = false, stopService = false)
        }
    }

    private fun finishActiveSession(completed: Boolean, stopService: Boolean) {
        if (finishingSession) return
        finishingSession = true
        focusTimer.stop()

        if (stopService) FocusSoundController.stop(getApplication())

        val now = System.currentTimeMillis()
        val state = _uiState.value
        val patch = activePatch ?: state.activePatch
        val compositionPatch = activeCompositionPatch ?: state.selectedComposition
        val instrumentSet = activeInstrumentSet ?: state.selectedInstrumentSet
        val playbackMode = activePlaybackMode
        val profile = patch.toSoundProfile()
        val startedAt = activeStartedAtMillis.takeIf { it > 0L } ?: now
        val elapsedSeconds = state.elapsedSeconds
            .takeIf { it > 0 }
            ?: ((now - startedAt) / 1000L).toInt().coerceAtLeast(0)

        val endedSession = FocusSession(
            mode = patch.mode,
            soundType = profile.soundType,
            durationMinutes = patch.durationMinutes,
            elapsedSeconds = elapsedSeconds,
            startedAtMillis = startedAt,
            endedAtMillis = now,
            focusRating = state.focusRating,
            fatigueRating = state.fatigueRating,
            brightness = patch.brightness,
            warmth = patch.warmth,
            movement = patch.movement,
            fatigueScore = compositionPatch?.fatigueScore ?: fatigueEstimator.estimate(profile, patch.durationMinutes),
            completed = completed,
            patchId = patch.id,
            patchName = patch.name,
            highCut = patch.highCut,
            stereoWidth = patch.stereoWidth,
            rainLayerAmount = patch.rainLayerAmount,
            padLayerAmount = patch.padLayerAmount,
            conditionSnapshotId = activeConditionSnapshotId,
            compositionPatchId = compositionPatch?.id,
            compositionPatchName = compositionPatch?.name,
            compositionGenre = compositionPatch?.genre,
            melodyDensity = compositionPatch?.melodyDensity ?: 0f,
            rhythmDensity = compositionPatch?.rhythmDensity ?: 0f,
            harmonicComplexity = compositionPatch?.harmonicComplexity ?: 0f
        )

        viewModelScope.launch {
            sessionRepository.saveSession(endedSession)
        }

        pendingSession = null
        activePatch = null
        activeCompositionPatch = null
        activeInstrumentSet = null
        activePlaybackMode = PlaybackMode.SOUND_DESIGN
        activeStartedAtMillis = 0L
        activeConditionSnapshotId = null

        _uiState.update {
            it.copy(
                screen = AppScreen.HOME,
                playbackState = FocusPlaybackState(
                    status = PlaybackStatus.STOPPED,
                    profile = profile,
                    patch = patch,
                    compositionPatch = compositionPatch,
                    instrumentSet = instrumentSet,
                    playbackMode = playbackMode
                ),
                remainingSeconds = patch.durationMinutes * 60,
                elapsedSeconds = elapsedSeconds,
                errorMessage = null
            )
        }
        resetFeedback()
        finishingSession = false
    }

    private fun applyCompositionToState(
        composition: CompositionPatch,
        patch: SoundPatch,
        instrumentSet: InstrumentSet?,
        playbackMode: PlaybackMode,
        screen: AppScreen
    ) {
        val profile = patch.toSoundProfile()
        _uiState.update {
            it.copy(
                screen = screen,
                selectedPatch = patch,
                generatedPatch = patch,
                selectedComposition = composition,
                generatedComposition = composition,
                selectedInstrumentSet = instrumentSet,
                playbackMode = playbackMode,
                preference = it.preference.copy(
                    mode = composition.mode,
                    soundType = profile.soundType,
                    brightness = profile.brightness,
                    warmth = profile.warmth,
                    movement = profile.movement,
                    timerMinutes = composition.durationMinutes
                ),
                durationMinutes = composition.durationMinutes,
                remainingSeconds = if (it.isRunning) it.remainingSeconds else composition.durationMinutes * 60,
                timerPreset = presetFor(composition.durationMinutes),
                fatigueScore = composition.fatigueScore,
                toneBrightness = patch.brightness,
                toneWarmth = patch.warmth,
                toneColdness = (1f - patch.warmth) * 0.22f,
                errorMessage = null
            )
        }
        viewModelScope.launch { preferenceRepository.save(_uiState.value.preference) }
    }

    private fun applyPatchToState(patch: SoundPatch, screen: AppScreen) {
        val profile = patch.toSoundProfile()
        val fatigueScore = fatigueEstimator.estimate(profile, patch.durationMinutes)
        _uiState.update {
            it.copy(
                screen = screen,
                selectedPatch = patch,
                generatedPatch = patch,
                selectedComposition = null,
                selectedInstrumentSet = null,
                playbackMode = PlaybackMode.SOUND_DESIGN,
                preference = it.preference.copy(
                    mode = profile.mode,
                    soundType = profile.soundType,
                    brightness = profile.brightness,
                    warmth = profile.warmth,
                    movement = profile.movement,
                    timerMinutes = patch.durationMinutes
                ),
                durationMinutes = patch.durationMinutes,
                remainingSeconds = if (it.isRunning) it.remainingSeconds else patch.durationMinutes * 60,
                timerPreset = presetFor(patch.durationMinutes),
                fatigueScore = fatigueScore,
                toneBrightness = patch.brightness,
                toneWarmth = patch.warmth,
                toneColdness = (1f - patch.warmth) * 0.22f
            )
        }
        viewModelScope.launch { preferenceRepository.save(_uiState.value.preference) }
    }

    private fun adjustSelectedComposition(
        warmthDelta: Float = 0f,
        melodyMultiplier: Float = 1f,
        rhythmMultiplier: Float = 1f,
        harmonicDelta: Float = 0f,
        padDelta: Float = 0f,
        brightnessDelta: Float = 0f,
        fatigueDelta: Int = 0,
        genre: CompositionGenre? = null,
        mode: FocusMode? = null,
        durationMinutes: Int? = null
    ) {
        val current = _uiState.value.selectedComposition ?: _uiState.value.generatedComposition ?: return
        val state = _uiState.value
        val adjustedMode = mode ?: current.mode
        val adjusted = current.copy(
            name = when (genre ?: current.genre) {
                CompositionGenre.LOFI -> "부드러운 로파이 집중곡"
                CompositionGenre.CLASSICAL_MINIMAL -> "미니멀 공부 모티프"
                CompositionGenre.ORCHESTRAL_PAD -> "오케스트라 패드 집중곡"
                CompositionGenre.SLEEP_DRONE -> "수면 드론 화성"
                CompositionGenre.AMBIENT_CODING -> "새벽 코딩 모티프"
            },
            mode = adjustedMode,
            genre = genre ?: current.genre,
            notes = current.notes.mapNotNull { note ->
                when (note.lane) {
                    NoteLane.MELODY -> if (melodyMultiplier < 0.7f && note.startBeat.toInt() % 2 != 0) null else note.copy(velocity = note.velocity * melodyMultiplier)
                    NoteLane.RHYTHM -> if (rhythmMultiplier < 0.6f && note.midiNote != 36) null else note.copy(velocity = note.velocity * rhythmMultiplier)
                    else -> note
                }
            },
            melodyDensity = (current.melodyDensity * melodyMultiplier).coerceIn(0.02f, 0.8f),
            rhythmDensity = (current.rhythmDensity * rhythmMultiplier).coerceIn(0.02f, 0.8f),
            harmonicComplexity = (current.harmonicComplexity + harmonicDelta).coerceIn(0.05f, 0.8f),
            padAmount = (current.padAmount + padDelta).coerceIn(0.15f, 1f),
            fatigueScore = (current.fatigueScore + fatigueDelta).coerceIn(5, 100),
            durationMinutes = durationMinutes ?: current.durationMinutes,
            instrumentNames = current.instrumentNames.ifEmpty { state.selectedInstrumentSet?.names.orEmpty() }
        )
        val patch = adjusted.toBackdropPatch(state.preference, state.playbackMode).copy(
            warmth = (state.activePatch.warmth + warmthDelta).coerceIn(0f, 1f),
            brightness = (state.activePatch.brightness + brightnessDelta).coerceIn(0f, 1f)
        )
        viewModelScope.launch {
            compositionRepository.save(adjusted)
            soundPatchRepository.save(patch)
        }
        applyCompositionToState(
            composition = adjusted,
            patch = patch,
            instrumentSet = state.selectedInstrumentSet,
            playbackMode = state.playbackMode,
            screen = AppScreen.AI_COMPOSER
        )
    }

    private fun exportComposition(exporter: (CompositionPatch, File) -> File) {
        val composition = _uiState.value.selectedComposition ?: _uiState.value.generatedComposition ?: run {
            _uiState.update { it.copy(exportMessage = "내보낼 작곡 결과가 없습니다.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(exportMessage = "내보내는 중입니다.") }
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    val baseDir = File(getApplication<Application>().getExternalFilesDir(null), "exports")
                    exporter(composition, baseDir)
                }
            }
            _uiState.update {
                it.copy(
                    exportMessage = result.fold(
                        onSuccess = { file -> "저장 위치: ${file.absolutePath}" },
                        onFailure = { error -> "내보내기 실패: ${error.message ?: "알 수 없는 오류"}" }
                    )
                )
            }
        }
    }

    private fun CompositionPatch.toBackdropPatch(
        preference: UserPreference,
        playbackMode: PlaybackMode = PlaybackMode.AI_COMPOSITION_ONLY
    ): SoundPatch {
        val profile = preference.copy(
            mode = mode,
            soundType = if (playbackMode == PlaybackMode.AI_COMPOSITION_ONLY) {
                SoundType.NONE
            } else {
                preference.soundType
            },
            brightness = when (genre) {
                CompositionGenre.SLEEP_DRONE -> 0.18f
                CompositionGenre.ORCHESTRAL_PAD -> 0.26f
                else -> 0.3f
            },
            warmth = when (genre) {
                CompositionGenre.SLEEP_DRONE -> 0.78f
                CompositionGenre.LOFI -> 0.72f
                else -> 0.66f
            },
            movement = when (genre) {
                CompositionGenre.SLEEP_DRONE -> 0.08f
                CompositionGenre.LOFI -> 0.2f
                else -> 0.14f
            },
            timerMinutes = durationMinutes
        ).toSoundProfile()
        val textureEnabled = playbackMode != PlaybackMode.AI_COMPOSITION_ONLY
        return profile.toSoundPatch(durationMinutes).copy(
            name = "$name 배경 레이어",
            highCut = when (genre) {
                CompositionGenre.SLEEP_DRONE -> 0.58f
                CompositionGenre.ORCHESTRAL_PAD -> 0.46f
                else -> 0.34f
            },
            lowAmount = when (genre) {
                CompositionGenre.SLEEP_DRONE -> 0.7f
                CompositionGenre.LOFI -> 0.64f
                else -> 0.56f
            },
            stereoWidth = if (mode == FocusMode.SLEEP) 0.28f else 0.44f,
            noiseLayerAmount = if (textureEnabled) profile.toSoundPatch(durationMinutes).noiseLayerAmount else 0f,
            rainLayerAmount = if (textureEnabled) profile.toSoundPatch(durationMinutes).rainLayerAmount else 0f,
            padLayerAmount = if (textureEnabled) {
                (padAmount * if (mode == FocusMode.SLEEP) 0.34f else 0.2f).coerceIn(0f, 0.45f)
            } else {
                0f
            },
            targetFatigueScore = fatigueScore
        )
    }

    private fun adjustSelectedPatch(
        brightnessDelta: Float = 0f,
        warmthDelta: Float = 0f,
        movementDelta: Float = 0f,
        rainDelta: Float = 0f
    ) {
        val current = _uiState.value.selectedPatch ?: _uiState.value.generatedPatch ?: return
        val adjusted = current.copy(
            brightness = (current.brightness + brightnessDelta).coerceIn(0f, 1f),
            warmth = (current.warmth + warmthDelta).coerceIn(0f, 1f),
            movement = (current.movement + movementDelta).coerceIn(0f, 1f),
            rainLayerAmount = (current.rainLayerAmount + rainDelta).coerceIn(0f, 1f),
            highCut = (current.highCut - brightnessDelta * 0.5f).coerceIn(0f, 1f)
        )
        applyPatchToState(adjusted, _uiState.value.screen)
    }

    private fun resetFeedback() {
        _uiState.update {
            it.copy(
                focusRating = 4,
                fatigueRating = 2,
                tooBright = false,
                tooMuffled = false,
                tooMuchBass = false,
                melodyAnnoying = false,
                rhythmAnnoying = false,
                harmonyLiked = false,
                tooRepetitive = false,
                focusedWell = false,
                tooDark = false,
                useAgain = false
            )
        }
    }

    private fun presetFor(minutes: Int): TimerPreset = when (minutes) {
        25 -> TimerPreset.TWENTY_FIVE
        50 -> TimerPreset.FIFTY
        else -> TimerPreset.CUSTOM
    }

    private data class StoredState(
        val preference: UserPreference,
        val sessions: List<FocusSession>,
        val presets: List<UserSoundPreset>,
        val weeklyReport: WeeklyReport
    )
}

private fun List<InstrumentPreset>.takeDefaultSetupInstruments(): List<InstrumentPreset> {
    return listOfNotNull(
        firstByName("리얼 업라이트 피아노"),
        firstByName("리얼 챔버 현악"),
        firstByName("리얼 콘트라베이스")
    ).ifEmpty { take(3) }
}

private fun List<InstrumentPreset>.takeDefaultLiveInstruments(task: MusicTask): List<InstrumentPreset> {
    return when (task) {
        MusicTask.SLEEP -> listOfNotNull(
            firstByName("펠트 피아노"),
            firstByName("넓은 현악 패드"),
            firstByName("리얼 챔버 현악")
        )
        MusicTask.READING -> listOfNotNull(
            firstByName("넓은 현악 패드"),
            firstByName("리얼 챔버 현악"),
            firstByName("리얼 콘트라베이스")
        )
        MusicTask.CODING -> listOfNotNull(
            firstByName("리얼 업라이트 피아노"),
            firstByName("넓은 현악 패드"),
            firstByName("리얼 콘트라베이스"),
            firstByName("약한 로파이 클릭")
        )
        MusicTask.RELAX -> listOfNotNull(
            firstByName("리얼 업라이트 피아노"),
            firstByName("리얼 챔버 현악"),
            firstByName("부드러운 플루트")
        )
        MusicTask.WORKOUT -> listOfNotNull(
            firstByName("리얼 업라이트 피아노"),
            firstByName("리얼 콘트라베이스"),
            firstByName("약한 로파이 클릭")
        )
        MusicTask.STUDY -> listOfNotNull(
            firstByName("리얼 업라이트 피아노"),
            firstByName("리얼 챔버 현악"),
            firstByName("리얼 콘트라베이스")
        )
    }.ifEmpty { takeDefaultSetupInstruments() }
}

private fun RecommendedInstrumentSet.resolve(presets: List<InstrumentPreset>): List<InstrumentPreset> = when (this) {
    RecommendedInstrumentSet.PIANO_STRINGS_BASS -> listOfNotNull(
        presets.firstByName("리얼 업라이트 피아노"),
        presets.firstByName("리얼 챔버 현악"),
        presets.firstByName("리얼 콘트라베이스")
    )
    RecommendedInstrumentSet.PIANO_SOLO -> listOfNotNull(
        presets.firstByName("펠트 피아노") ?: presets.firstByName("리얼 업라이트 피아노")
    )
    RecommendedInstrumentSet.STRING_PAD -> listOfNotNull(
        presets.firstByName("리얼 챔버 현악"),
        presets.firstByName("넓은 현악 패드")
    )
    RecommendedInstrumentSet.PAD_BASS -> listOfNotNull(
        presets.firstByName("넓은 현악 패드"),
        presets.firstByName("리얼 콘트라베이스")
    )
    RecommendedInstrumentSet.FELT_PIANO_CELLO -> listOfNotNull(
        presets.firstByName("펠트 피아노"),
        presets.firstByName("리얼 챔버 현악")
    )
}.ifEmpty { presets.takeDefaultSetupInstruments() }

private fun List<InstrumentPreset>.firstByName(name: String): InstrumentPreset? {
    return firstOrNull { it.name == name }
}

private fun List<InstrumentPreset>.toInstrumentSet(): InstrumentSet {
    return InstrumentSet(
        melody = firstOrNull { it.role == com.focussound.instrument.InstrumentRole.MELODY },
        pad = firstOrNull { it.role == com.focussound.instrument.InstrumentRole.PAD },
        bass = firstOrNull { it.role == com.focussound.instrument.InstrumentRole.BASS },
        rhythm = firstOrNull { it.role == com.focussound.instrument.InstrumentRole.RHYTHM }
    )
}

private fun MusicTask.toFocusMode(): FocusMode = when (this) {
    MusicTask.STUDY -> FocusMode.STUDY
    MusicTask.SLEEP -> FocusMode.SLEEP
    MusicTask.CODING -> FocusMode.CODING
    MusicTask.READING -> FocusMode.READING
    MusicTask.RELAX -> FocusMode.READING
    MusicTask.WORKOUT -> FocusMode.CODING
}

private fun MusicTask.defaultMusicStyle(): MusicStyle = when (this) {
    MusicTask.STUDY -> MusicStyle.RELAXING_PIANO
    MusicTask.SLEEP -> MusicStyle.SLEEP_DRONE
    MusicTask.CODING -> MusicStyle.LOFI
    MusicTask.READING -> MusicStyle.ORCHESTRAL_PAD
    MusicTask.RELAX -> MusicStyle.RELAXING_PIANO
    MusicTask.WORKOUT -> MusicStyle.MINIMAL_ELECTRONIC
}

private fun MusicTask.defaultMelodyAmount(): Float = when (this) {
    MusicTask.SLEEP -> 0.12f
    MusicTask.READING -> 0.14f
    MusicTask.STUDY -> 0.26f
    MusicTask.RELAX -> 0.32f
    MusicTask.CODING -> 0.34f
    MusicTask.WORKOUT -> 0.48f
}

private fun MusicTask.defaultRhythmAmount(): Float = when (this) {
    MusicTask.SLEEP,
    MusicTask.READING -> 0f
    MusicTask.STUDY,
    MusicTask.RELAX -> 0.08f
    MusicTask.CODING -> 0.22f
    MusicTask.WORKOUT -> 0.78f
}

private fun MusicTask.defaultDiversity(): Float = when (this) {
    MusicTask.SLEEP -> 0.28f
    MusicTask.READING -> 0.32f
    MusicTask.STUDY -> 0.44f
    MusicTask.RELAX -> 0.48f
    MusicTask.CODING -> 0.56f
    MusicTask.WORKOUT -> 0.68f
}

private fun MusicStyle.toCompositionStyle(): CompositionStyle = when (this) {
    MusicStyle.RELAXING_PIANO -> CompositionStyle.PIANO_SOLO
    MusicStyle.AMBIENT -> CompositionStyle.AMBIENT
    MusicStyle.LOFI -> CompositionStyle.LOFI
    MusicStyle.CLASSICAL_MINIMAL -> CompositionStyle.CLASSICAL_MINIMAL
    MusicStyle.ORCHESTRAL_PAD -> CompositionStyle.ORCHESTRAL_PAD
    MusicStyle.SLEEP_DRONE -> CompositionStyle.SLEEP_DRONE
    MusicStyle.MINIMAL_ELECTRONIC -> CompositionStyle.LOFI
}

private fun FocusMode.defaultCompositionStyle(): CompositionStyle = when (this) {
    FocusMode.STUDY -> CompositionStyle.CLASSICAL_MINIMAL
    FocusMode.CODING -> CompositionStyle.LOFI
    FocusMode.READING -> CompositionStyle.ORCHESTRAL_PAD
    FocusMode.SLEEP -> CompositionStyle.SLEEP_DRONE
}

private fun FocusMode.randomCompositionStyle(random: Random): CompositionStyle = when (this) {
    FocusMode.STUDY -> listOf(
        CompositionStyle.CLASSICAL_MINIMAL,
        CompositionStyle.PIANO_SOLO,
        CompositionStyle.AMBIENT,
        CompositionStyle.ORCHESTRAL_PAD
    )
    FocusMode.CODING -> listOf(
        CompositionStyle.LOFI,
        CompositionStyle.AMBIENT,
        CompositionStyle.CLASSICAL_MINIMAL
    )
    FocusMode.READING -> listOf(
        CompositionStyle.ORCHESTRAL_PAD,
        CompositionStyle.AMBIENT,
        CompositionStyle.PIANO_SOLO
    )
    FocusMode.SLEEP -> listOf(
        CompositionStyle.SLEEP_DRONE,
        CompositionStyle.AMBIENT,
        CompositionStyle.ORCHESTRAL_PAD
    )
}.random(random)

private fun FocusMode.randomCompositionMood(random: Random): CompositionMood = when (this) {
    FocusMode.STUDY -> listOf(
        CompositionMood.CALM,
        CompositionMood.WARM,
        CompositionMood.DAWN,
        CompositionMood.SPACIOUS,
        CompositionMood.DREAMY
    )
    FocusMode.CODING -> listOf(
        CompositionMood.DAWN,
        CompositionMood.CALM,
        CompositionMood.RAINY,
        CompositionMood.SPACIOUS,
        CompositionMood.DARK
    )
    FocusMode.READING -> listOf(
        CompositionMood.WARM,
        CompositionMood.CALM,
        CompositionMood.RAINY,
        CompositionMood.DREAMY
    )
    FocusMode.SLEEP -> listOf(
        CompositionMood.DARK,
        CompositionMood.DREAMY,
        CompositionMood.SPACIOUS,
        CompositionMood.CALM
    )
}.random(random)

private fun FocusMode.randomFocusIntensity(random: Random): FocusIntensity = when (this) {
    FocusMode.SLEEP -> FocusIntensity.LOW
    FocusMode.READING -> listOf(FocusIntensity.LOW, FocusIntensity.MEDIUM).random(random)
    FocusMode.STUDY -> listOf(FocusIntensity.LOW, FocusIntensity.MEDIUM, FocusIntensity.CLEAR).random(random)
    FocusMode.CODING -> listOf(FocusIntensity.MEDIUM, FocusIntensity.CLEAR, FocusIntensity.LOW).random(random)
}

private fun CompositionSetup.toCompositionIntent(durationMinutes: Int): CompositionIntent {
    val mode = when (style) {
        CompositionStyle.SLEEP_DRONE -> FocusMode.SLEEP
        CompositionStyle.PIANO_SOLO,
        CompositionStyle.CLASSICAL_MINIMAL -> FocusMode.STUDY
        CompositionStyle.LOFI -> FocusMode.CODING
        else -> this.mode
    }
    val melodyDensity = when (style) {
        CompositionStyle.SLEEP_DRONE -> 0.03f
        CompositionStyle.AMBIENT -> 0.12f
        CompositionStyle.PIANO_SOLO -> 0.38f
        CompositionStyle.CLASSICAL_MINIMAL -> 0.3f
        CompositionStyle.LOFI -> 0.24f
        CompositionStyle.ORCHESTRAL_PAD -> 0.14f
    }
    val rhythmDensity = when (style) {
        CompositionStyle.LOFI -> 0.16f
        CompositionStyle.PIANO_SOLO,
        CompositionStyle.CLASSICAL_MINIMAL -> 0.06f
        else -> 0.02f
    }
    val intensityOffset = when (focusIntensity) {
        FocusIntensity.LOW -> -0.04f
        FocusIntensity.MEDIUM -> 0f
        FocusIntensity.CLEAR -> 0.05f
    }
    val moodTempoOffset = when (mood) {
        CompositionMood.DAWN -> -2
        CompositionMood.CALM -> -4
        CompositionMood.WARM -> -3
        CompositionMood.DARK -> -7
        CompositionMood.RAINY -> -5
        CompositionMood.SPACIOUS -> -6
        CompositionMood.DREAMY -> -8
    }
    val moodMelodyOffset = when (mood) {
        CompositionMood.DAWN -> 0.03f
        CompositionMood.CALM -> -0.04f
        CompositionMood.WARM -> -0.02f
        CompositionMood.DARK -> -0.07f
        CompositionMood.RAINY -> -0.03f
        CompositionMood.SPACIOUS -> -0.06f
        CompositionMood.DREAMY -> -0.08f
    }
    val moodRhythmOffset = when (mood) {
        CompositionMood.DAWN -> 0.01f
        CompositionMood.CALM -> -0.02f
        CompositionMood.WARM -> -0.02f
        CompositionMood.DARK -> -0.04f
        CompositionMood.RAINY -> -0.01f
        CompositionMood.SPACIOUS -> -0.04f
        CompositionMood.DREAMY -> -0.05f
    }
    val moodHarmonicOffset = when (mood) {
        CompositionMood.DAWN -> 0.02f
        CompositionMood.CALM -> -0.03f
        CompositionMood.WARM -> 0.04f
        CompositionMood.DARK -> 0.06f
        CompositionMood.RAINY -> 0.03f
        CompositionMood.SPACIOUS -> 0.01f
        CompositionMood.DREAMY -> 0.08f
    }
    val moodPadOffset = when (mood) {
        CompositionMood.DAWN -> 0.04f
        CompositionMood.CALM -> 0.08f
        CompositionMood.WARM -> 0.12f
        CompositionMood.DARK -> 0.1f
        CompositionMood.RAINY -> 0.06f
        CompositionMood.SPACIOUS -> 0.16f
        CompositionMood.DREAMY -> 0.18f
    }
    return CompositionIntent(
        mode = mode,
        genre = style.genre,
        moodKeywords = listOf(mood.label, style.label, focusIntensity.label),
        fatigueTarget = when (focusIntensity) {
            FocusIntensity.LOW -> FatigueTarget.VERY_LOW
            FocusIntensity.MEDIUM -> FatigueTarget.LOW
            FocusIntensity.CLEAR -> FatigueTarget.NORMAL
        },
        keyHint = null,
        tempoHintBpm = (when (style) {
            CompositionStyle.SLEEP_DRONE -> 54
            CompositionStyle.LOFI -> 74
            CompositionStyle.PIANO_SOLO -> 68
            CompositionStyle.CLASSICAL_MINIMAL -> 70
            CompositionStyle.ORCHESTRAL_PAD -> 62
            CompositionStyle.AMBIENT -> 66
        } + moodTempoOffset).coerceIn(48, 88),
        melodyDensityHint = (melodyDensity + intensityOffset + moodMelodyOffset).coerceIn(0.02f, 0.65f),
        rhythmDensityHint = (rhythmDensity + intensityOffset * 0.5f + moodRhythmOffset).coerceIn(0.01f, 0.32f),
        harmonicComplexityHint = (when (style) {
            CompositionStyle.ORCHESTRAL_PAD -> 0.42f
            CompositionStyle.LOFI -> 0.36f
            CompositionStyle.SLEEP_DRONE -> 0.18f
            else -> 0.28f
        } + moodHarmonicOffset).coerceIn(0.08f, 0.72f),
        padFocus = style != CompositionStyle.PIANO_SOLO,
        durationMinutes = durationMinutes.coerceIn(5, 180),
        padAmountHint = (when (style) {
            CompositionStyle.PIANO_SOLO -> 0.18f
            CompositionStyle.SLEEP_DRONE -> 0.9f
            CompositionStyle.ORCHESTRAL_PAD -> 0.82f
            else -> 0.62f
        } + moodPadOffset).coerceIn(0.12f, 1f)
    )
}
