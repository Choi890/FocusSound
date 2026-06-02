package com.focussound.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focussound.ui.home.HomeScreen
import com.focussound.ui.ai.AiSoundDesignerScreen
import com.focussound.ui.condition.ConditionCheckScreen
import com.focussound.ui.composer.LocalComposerScreen
import com.focussound.ui.instrument.InstrumentPackScreen
import com.focussound.ui.player.PlayerScreen
import com.focussound.ui.profile.SoundTasteProfileScreen
import com.focussound.ui.report.WeeklyReportScreen
import com.focussound.ui.setup.CompositionResultScreen
import com.focussound.ui.setup.InstrumentSelectionScreen
import com.focussound.ui.setup.MoodStyleSelectionScreen
import com.focussound.ui.setup.SoundTypeSelectionScreen
import com.focussound.ui.sounddesign.SoundDesignScreen

@Composable
fun FocusSoundApp(viewModel: FocusSoundViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = state.screen != AppScreen.HOME) {
        viewModel.onBack()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state.screen) {
            AppScreen.HOME -> HomeScreen(
                state = state,
                onTaskSelected = viewModel::selectMusicTask,
                onStyleSelected = viewModel::selectMusicStyle,
                onSoundTypeSelected = viewModel::selectSoundType,
                onInstrumentSelected = viewModel::toggleSetupInstrument,
                onBrightnessChanged = viewModel::updateToneBrightness,
                onWarmthChanged = viewModel::updateToneWarmth,
                onColdnessChanged = viewModel::updateToneColdness,
                onMelodyAmountChanged = viewModel::updateLiveMelodyAmount,
                onRhythmAmountChanged = viewModel::updateLiveRhythmAmount,
                onDiversityChanged = viewModel::updateLiveDiversity,
                onGenerateMusic = viewModel::generateHomeCompositionAndPlay,
                onStop = viewModel::stopFocusSession
            )

            AppScreen.AI_DESIGNER -> AiSoundDesignerScreen(
                state = state,
                onBack = viewModel::onBack,
                onPromptChanged = viewModel::updateAiPrompt,
                onGenerate = viewModel::generatePatchFromPrompt,
                onStart = viewModel::startFocusSession,
                onMakeWarmer = viewModel::makeGeneratedWarmer,
                onReduceRain = viewModel::reduceGeneratedRain,
                onReduceMovement = viewModel::reduceGeneratedMovement
            )

            AppScreen.AI_COMPOSER -> LocalComposerScreen(
                state = state,
                onBack = viewModel::onBack,
                onPromptChanged = viewModel::updateCompositionPrompt,
                onGenerate = viewModel::generateCompositionFromPrompt,
                onStart = viewModel::startFocusSession,
                onStop = viewModel::stopFocusSession,
                onSave = viewModel::saveGeneratedComposition,
                onExport = viewModel::showExportDialog,
                onExportWav = viewModel::exportCompositionAsWav,
                onExportMidi = viewModel::exportCompositionAsMidi,
                onDismissExport = viewModel::hideExportDialog,
                onMakeWarmer = viewModel::makeCompositionWarmer,
                onReduceMelody = viewModel::reduceCompositionMelody,
                onReduceRhythm = viewModel::reduceCompositionRhythm,
                onMakeDarker = viewModel::makeCompositionDarker,
                onIncreasePad = viewModel::increaseCompositionPad,
                onSleep = viewModel::makeCompositionSleepReady
            )

            AppScreen.CONDITION_CHECK -> ConditionCheckScreen(
                state = state,
                onBack = viewModel::onBack,
                onSave = viewModel::updateCondition
            )

            AppScreen.TASTE_PROFILE -> SoundTasteProfileScreen(
                state = state,
                onBack = viewModel::onBack
            )

            AppScreen.SOUND_DESIGN -> SoundDesignScreen(
                state = state,
                onBack = viewModel::onBack,
                onBrightnessChanged = viewModel::updateBrightness,
                onWarmthChanged = viewModel::updateWarmth,
                onMovementChanged = viewModel::updateMovement,
                onSavePreset = viewModel::saveCurrentPreset,
                onPlayer = viewModel::goToPlayer
            )

            AppScreen.PLAYER -> PlayerScreen(
                state = state,
                onBack = viewModel::onBack,
                onPresetSelected = viewModel::selectTimerPreset,
                onCustomMinutesChanged = viewModel::updateCustomTimerMinutes,
                onStart = viewModel::startFocusSession,
                onPause = viewModel::pausePlayback,
                onResume = viewModel::resumePlayback,
                onStop = viewModel::stopFocusSession,
                onToneBrightnessChanged = viewModel::updateToneBrightness,
                onToneWarmthChanged = viewModel::updateToneWarmth,
                onToneColdnessChanged = viewModel::updateToneColdness,
                onSoundDesign = viewModel::goToSoundDesign
            )

            AppScreen.WEEKLY_REPORT -> WeeklyReportScreen(
                state = state,
                onBack = viewModel::onBack
            )

            AppScreen.INSTRUMENT_PACK -> InstrumentPackScreen(
                state = state,
                onBack = viewModel::onBack,
                onImportWav = viewModel::importWavInstrument,
                onImportSf2 = viewModel::importSf2Instrument
            )

            AppScreen.INSTRUMENT_SELECTION -> InstrumentSelectionScreen(
                state = state,
                onBack = viewModel::onBack,
                onToggleInstrument = viewModel::toggleSetupInstrument,
                onRecommendedSet = viewModel::applyRecommendedInstrumentSet,
                onNext = viewModel::goToMoodStyleSelection
            )

            AppScreen.MOOD_STYLE_SELECTION -> MoodStyleSelectionScreen(
                state = state,
                onBack = viewModel::onBack,
                onMoodSelected = viewModel::selectSetupMood,
                onStyleSelected = viewModel::selectSetupStyle,
                onIntensitySelected = viewModel::selectSetupIntensity,
                onNext = viewModel::goToSoundTypeSelection
            )

            AppScreen.SOUND_TYPE_SELECTION -> SoundTypeSelectionScreen(
                state = state,
                onBack = viewModel::onBack,
                onSoundTypeSelected = viewModel::selectSetupSoundType,
                onGenerate = viewModel::generateCompositionFromSetup
            )

            AppScreen.COMPOSITION_RESULT -> CompositionResultScreen(
                state = state,
                onBack = viewModel::onBack,
                onPlay = viewModel::startFocusSession,
                onRegenerate = viewModel::generateCompositionFromSetup,
                onChangeInstruments = viewModel::startCompositionSetup,
                onChangeMood = viewModel::goToMoodStyleSelection,
                onExport = viewModel::showExportDialog,
                onExportWav = viewModel::exportCompositionAsWav,
                onExportMidi = viewModel::exportCompositionAsMidi,
                onDismissExport = viewModel::hideExportDialog
            )
        }
    }
}
