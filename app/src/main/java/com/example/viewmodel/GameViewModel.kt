package com.example.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdManager
import com.example.ads.AdRewardReason
import com.example.data.LevelRepository
import com.example.data.UserGameData
import com.example.data.UserPreferencesRepository
import com.example.data.models.LevelConfig
import com.example.game.HapticType
import com.example.game.HapticsManager
import com.example.game.SoundManager
import com.example.game.SoundType
import com.example.ui.components.BombMood
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameplayUiState(
    val currentLevel: LevelConfig? = null,
    val secondsRemaining: Int = 60,
    val isTimerRunning: Boolean = false,
    val activeMechanicIndex: Int = 0,
    val solvedMechanicIndices: Set<Int> = emptySet(),
    val bombMood: BombMood = BombMood.NORMAL,
    val isHintActive: Boolean = false,
    val freeHintsRemaining: Int = 1,
    val isPaused: Boolean = false,
    val isLevelComplete: Boolean = false,
    val isGameOver: Boolean = false,
    val gameOverReason: String = "",
    val starsEarned: Int = 0,
    val calculatedScore: Int = 0,
    val showTutorialOverlay: Boolean = false,
    val showStoreDialog: Boolean = false,
    val storeMessage: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferencesRepository(application)
    val soundManager = SoundManager(application)
    val hapticsManager = HapticsManager(application)
    val adManager = AdManager(application)

    private val _userGameData = MutableStateFlow(UserGameData())
    val userGameData: StateFlow<UserGameData> = _userGameData.asStateFlow()

    private val _gameplayState = MutableStateFlow(GameplayUiState())
    val gameplayState: StateFlow<GameplayUiState> = _gameplayState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            userPrefs.userGameData.collectLatest { data ->
                _userGameData.value = data
                soundManager.setEnabled(data.soundEnabled)
                hapticsManager.setEnabled(data.hapticsEnabled)
            }
        }

        // Background life auto-refill ticker (checks every 1 second)
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _userGameData.value
                val now = System.currentTimeMillis()
                val (effectiveLives, _) = current.getEffectiveLives(now)
                if (effectiveLives > current.lives) {
                    userPrefs.addLife(now)
                }
            }
        }
    }

    fun loadLevel(levelNumber: Int) {
        val level = LevelRepository.getLevel(levelNumber) ?: return
        timerJob?.cancel()

        _gameplayState.value = GameplayUiState(
            currentLevel = level,
            secondsRemaining = level.timeLimitSeconds,
            isTimerRunning = false,
            activeMechanicIndex = 0,
            solvedMechanicIndices = emptySet(),
            bombMood = BombMood.NORMAL,
            isHintActive = false,
            freeHintsRemaining = level.freeHintsCount,
            showTutorialOverlay = true, // Mission Briefing modal opens on level load
            isPaused = false,
            isLevelComplete = false,
            isGameOver = false
        )
        // Timer strictly paused until "PROCEED WITH DEFUSAL" is clicked
    }

    fun dismissTutorialAndStart() {
        _gameplayState.update { it.copy(showTutorialOverlay = false) }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        _gameplayState.update { it.copy(isTimerRunning = true) }

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _gameplayState.value
                if (!current.isTimerRunning || current.isPaused || current.isLevelComplete || current.isGameOver) {
                    continue
                }

                val newSec = current.secondsRemaining - 1
                if (newSec <= 0) {
                    _gameplayState.update {
                        it.copy(
                            secondsRemaining = 0,
                            bombMood = BombMood.EXPLODED
                        )
                    }
                    triggerExplosion("Timer expired! The bomb detonated!")
                    break
                } else {
                    val mood = if (newSec <= 10) BombMood.NERVOUS else BombMood.NORMAL
                    if (newSec <= 10) {
                        soundManager.playSound(SoundType.TIMER_TICK)
                    }
                    _gameplayState.update {
                        it.copy(secondsRemaining = newSec, bombMood = mood)
                    }
                }
            }
        }
    }

    fun pauseGame() {
        _gameplayState.update { it.copy(isPaused = true) }
    }

    fun resumeGame() {
        _gameplayState.update { it.copy(isPaused = false) }
    }

    fun restartCurrentLevel() {
        val lvl = _gameplayState.value.currentLevel?.levelNumber ?: 1
        loadLevel(lvl)
    }

    fun onWireCut(wireIndex: Int, isCorrect: Boolean) {
        soundManager.playSound(SoundType.BUTTON_TAP)
        if (isCorrect) {
            soundManager.playSound(SoundType.WIRE_CUT_SUCCESS)
            hapticsManager.triggerHaptic(HapticType.WIRE_CUT)
            advanceMechanic()
        } else {
            soundManager.playSound(SoundType.WIRE_CUT_FAIL)
            hapticsManager.triggerHaptic(HapticType.INCORRECT_INPUT)
            triggerExplosion("Wrong wire cut! The bomb detonated!")
        }
    }

    fun onCodeEntered(isCorrect: Boolean) {
        if (isCorrect) {
            soundManager.playSound(SoundType.WIRE_CUT_SUCCESS)
            hapticsManager.triggerHaptic(HapticType.CORRECT_INPUT)
            advanceMechanic()
        } else {
            soundManager.playSound(SoundType.WIRE_CUT_FAIL)
            hapticsManager.triggerHaptic(HapticType.INCORRECT_INPUT)
        }
    }

    fun onSymbolMatched(isCorrect: Boolean) {
        if (isCorrect) {
            soundManager.playSound(SoundType.WIRE_CUT_SUCCESS)
            hapticsManager.triggerHaptic(HapticType.CORRECT_INPUT)
            advanceMechanic()
        } else {
            soundManager.playSound(SoundType.WIRE_CUT_FAIL)
            hapticsManager.triggerHaptic(HapticType.INCORRECT_INPUT)
        }
    }

    fun onButtonSequenceCompleted(isCorrect: Boolean) {
        if (isCorrect) {
            soundManager.playSound(SoundType.WIRE_CUT_SUCCESS)
            hapticsManager.triggerHaptic(HapticType.CORRECT_INPUT)
            advanceMechanic()
        } else {
            soundManager.playSound(SoundType.WIRE_CUT_FAIL)
            hapticsManager.triggerHaptic(HapticType.INCORRECT_INPUT)
        }
    }

    fun onSpeedTappingCompleted(isCorrect: Boolean) {
        if (isCorrect) {
            soundManager.playSound(SoundType.WIRE_CUT_SUCCESS)
            hapticsManager.triggerHaptic(HapticType.CORRECT_INPUT)
            advanceMechanic()
        } else {
            soundManager.playSound(SoundType.WIRE_CUT_FAIL)
            hapticsManager.triggerHaptic(HapticType.INCORRECT_INPUT)
        }
    }

    fun onFrequencyTuned(isCorrect: Boolean) {
        if (isCorrect) {
            soundManager.playSound(SoundType.WIRE_CUT_SUCCESS)
            hapticsManager.triggerHaptic(HapticType.CORRECT_INPUT)
            advanceMechanic()
        } else {
            soundManager.playSound(SoundType.WIRE_CUT_FAIL)
            hapticsManager.triggerHaptic(HapticType.INCORRECT_INPUT)
        }
    }

    fun onPolarityCompleted(isCorrect: Boolean) {
        if (isCorrect) {
            soundManager.playSound(SoundType.WIRE_CUT_SUCCESS)
            hapticsManager.triggerHaptic(HapticType.CORRECT_INPUT)
            advanceMechanic()
        } else {
            soundManager.playSound(SoundType.WIRE_CUT_FAIL)
            hapticsManager.triggerHaptic(HapticType.INCORRECT_INPUT)
        }
    }

    fun onArithmeticAnswered(isCorrect: Boolean) {
        if (isCorrect) {
            soundManager.playSound(SoundType.WIRE_CUT_SUCCESS)
            hapticsManager.triggerHaptic(HapticType.CORRECT_INPUT)
            advanceMechanic()
        } else {
            soundManager.playSound(SoundType.WIRE_CUT_FAIL)
            hapticsManager.triggerHaptic(HapticType.INCORRECT_INPUT)
        }
    }

    fun onRotaryDialCompleted(isCorrect: Boolean) {
        if (isCorrect) {
            soundManager.playSound(SoundType.WIRE_CUT_SUCCESS)
            hapticsManager.triggerHaptic(HapticType.CORRECT_INPUT)
            advanceMechanic()
        } else {
            soundManager.playSound(SoundType.WIRE_CUT_FAIL)
            hapticsManager.triggerHaptic(HapticType.INCORRECT_INPUT)
        }
    }

    fun onCircuitFusesCompleted(isCorrect: Boolean) {
        if (isCorrect) {
            soundManager.playSound(SoundType.WIRE_CUT_SUCCESS)
            hapticsManager.triggerHaptic(HapticType.CORRECT_INPUT)
            advanceMechanic()
        } else {
            soundManager.playSound(SoundType.WIRE_CUT_FAIL)
            hapticsManager.triggerHaptic(HapticType.INCORRECT_INPUT)
        }
    }

    private fun advanceMechanic() {
        val current = _gameplayState.value
        val level = current.currentLevel ?: return
        val currentIdx = current.activeMechanicIndex

        val newSolved = current.solvedMechanicIndices + currentIdx
        val nextIdx = currentIdx + 1

        if (nextIdx >= level.mechanics.size) {
            // Level completely disarmed!
            triggerLevelComplete()
        } else {
            _gameplayState.update {
                it.copy(
                    activeMechanicIndex = nextIdx,
                    solvedMechanicIndices = newSolved,
                    isHintActive = false
                )
            }
        }
    }

    private fun triggerLevelComplete() {
        timerJob?.cancel()
        val current = _gameplayState.value
        val level = current.currentLevel ?: return

        val totalTime = level.timeLimitSeconds
        val remaining = current.secondsRemaining
        val timeRatio = remaining.toFloat() / totalTime.toFloat()

        // Stars: 3 stars if >= 50% time remaining, 2 stars if >= 20% time remaining, 1 star otherwise
        val stars = when {
            timeRatio >= 0.50f -> 3
            timeRatio >= 0.20f -> 2
            else -> 1
        }
        val score = (remaining * 100) + (stars * 500)

        soundManager.playSound(SoundType.LEVEL_COMPLETE)
        hapticsManager.triggerHaptic(HapticType.LEVEL_COMPLETE)

        _gameplayState.update {
            it.copy(
                isLevelComplete = true,
                bombMood = BombMood.DEFUSED,
                starsEarned = stars,
                calculatedScore = score
            )
        }

        viewModelScope.launch {
            userPrefs.completeLevel(level.levelNumber, stars)
        }
    }

    private fun triggerExplosion(reason: String) {
        timerJob?.cancel()
        soundManager.playSound(SoundType.EXPLOSION)
        hapticsManager.triggerHaptic(HapticType.EXPLOSION)

        _gameplayState.update {
            it.copy(
                isGameOver = true,
                bombMood = BombMood.EXPLODED,
                gameOverReason = reason
            )
        }

        viewModelScope.launch {
            userPrefs.loseLife(System.currentTimeMillis())
        }
    }

    fun requestHint(activity: Activity) {
        // Tips/Hints are revealed with Ads only
        adManager.showRewardedAd(
            activity = activity,
            reason = AdRewardReason.EXTRA_HINT,
            onRewardEarned = {
                _gameplayState.update { it.copy(isHintActive = true) }
                hapticsManager.triggerHaptic(HapticType.TAP)
            }
        )
    }

    fun watchAdForLife(activity: Activity) {
        adManager.showRewardedAd(
            activity = activity,
            reason = AdRewardReason.REFILL_LIFE,
            onRewardEarned = {
                viewModelScope.launch {
                    userPrefs.addLife(System.currentTimeMillis())
                }
            }
        )
    }

    fun toggleSound() {
        val newSound = !_userGameData.value.soundEnabled
        viewModelScope.launch {
            userPrefs.setSound(newSound)
        }
    }

    fun toggleHaptics() {
        val newHaptics = !_userGameData.value.hapticsEnabled
        viewModelScope.launch {
            userPrefs.setHaptics(newHaptics)
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            userPrefs.resetProgress()
        }
    }

    fun openStore() {
        _gameplayState.update { it.copy(showStoreDialog = true) }
    }

    fun closeStore() {
        _gameplayState.update { it.copy(showStoreDialog = false, storeMessage = null) }
    }

    fun simulateStorePurchase(itemName: String) {
        viewModelScope.launch {
            if (itemName.contains("Life", ignoreCase = true) || itemName.contains("Energy", ignoreCase = true)) {
                userPrefs.refillLives()
            }
            _gameplayState.update {
                it.copy(storeMessage = "Purchased $itemName! (Simulated IAP)")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        soundManager.release()
    }
}
