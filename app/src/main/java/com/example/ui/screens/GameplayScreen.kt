package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelRepository
import com.example.data.models.MechanicData
import com.example.data.models.SectorNavigationUtils
import com.example.ui.components.CartoonBombView
import com.example.ui.components.GameOverDialog
import com.example.ui.components.LevelCompleteDialog
import com.example.ui.components.LivesHud
import com.example.ui.components.PauseDialog
import com.example.ui.components.StoreDialog
import com.example.ui.components.TimerHud
import com.example.ui.components.TutorialOverlay
import com.example.ui.components.mechanics.PuzzleArithmetic
import com.example.ui.components.mechanics.PuzzleButtonSequence
import com.example.ui.components.mechanics.PuzzleCircuitFuses
import com.example.ui.components.mechanics.PuzzleCodeEntry
import com.example.ui.components.mechanics.PuzzleFrequencyTuning
import com.example.ui.components.mechanics.PuzzlePolarityMatching
import com.example.ui.components.mechanics.PuzzleRotaryDial
import com.example.ui.components.mechanics.PuzzleSpeedTapping
import com.example.ui.components.mechanics.PuzzleSymbolMatching
import com.example.ui.components.mechanics.PuzzleWireCutting
import com.example.ui.theme.ImmersiveBg
import com.example.ui.theme.ImmersiveCaption
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMidPurple
import com.example.ui.theme.ImmersiveMuted
import com.example.ui.theme.ImmersiveSoftLilac
import com.example.ui.theme.ImmersiveText
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@Composable
fun GameplayScreen(
    viewModel: GameViewModel,
    onBackToMenu: () -> Unit,
    onLevelSelect: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val userGameData by viewModel.userGameData.collectAsState()
    val gameState by viewModel.gameplayState.collectAsState()
    val currentLevel = gameState.currentLevel ?: return

    val (_, remainingRefillMs) = userGameData.getEffectiveLives(System.currentTimeMillis())
    val activeMechanic = currentLevel.mechanics.getOrNull(gameState.activeMechanicIndex)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ImmersiveBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ImmersiveLavender.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(600f, 120f),
                        radius = 800f
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top HUD: Two-row eye-catching tactical layout with generous gaps and clear alignment
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(ImmersiveCard)
                        .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(22.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    val (sectorNum, levelInSec) = SectorNavigationUtils.getSectorAndLevel(currentLevel.levelNumber)
                    val sectorInfo = LevelRepository.getSectorInfo(sectorNum)
                    val sectorSubtitle = if (currentLevel.isTutorial) "TRAINING GROUNDS" else sectorInfo.name.removePrefix("SECTOR $sectorNum: ")

                    // Row 1: Pause button, Mission/Sector Breadcrumb & Title, and Hint (Ad-only) button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pause Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF211F24))
                                .border(1.dp, ImmersiveCardBorder, CircleShape)
                                .clickable { viewModel.pauseGame() }
                                .testTag("gameplay_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Pause",
                                tint = ImmersiveSoftLilac,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Center Sector & Mission Details
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = if (currentLevel.isTutorial) "TUTORIAL $levelInSec/8 • $sectorSubtitle" else "SECTOR $sectorNum • LEVEL $levelInSec",
                                color = ImmersiveLavender,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                maxLines = 1
                            )
                            Text(
                                text = currentLevel.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        // Hint Button - Intel Clue revealed with Ads only
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(ImmersiveMidPurple)
                                .border(1.dp, ImmersiveLavender.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                                .clickable {
                                    if (activity != null) {
                                        viewModel.requestHint(activity)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                .testTag("gameplay_hint_button"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OndemandVideo,
                                contentDescription = "Watch Ad for Hint",
                                tint = ImmersiveLavender,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "HINT",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 2: Timer HUD and Energy (Lives) HUD with comfortable spacing and store link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimerHud(secondsRemaining = gameState.secondsRemaining)

                        LivesHud(
                            lives = userGameData.lives,
                            remainingRefillMs = remainingRefillMs,
                            onAddLivesClick = {
                                if (activity != null) {
                                    viewModel.watchAdForLife(activity)
                                }
                            },
                            onOpenStoreClick = {
                                viewModel.openStore()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hero Bomb Display Card (Immersive UI rounded-3xl container)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(ImmersiveCard)
                        .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(28.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CartoonBombView(
                            size = 135.dp,
                            mood = gameState.bombMood,
                            shakeIntensity = if (gameState.secondsRemaining <= 10) 1f else 0f
                        )

                        // Multi-mechanic Progress Indicator
                        if (currentLevel.mechanics.size > 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ImmersiveMidPurple)
                                    .border(1.dp, ImmersiveLavender, CircleShape)
                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "STAGE ${gameState.activeMechanicIndex + 1} OF ${currentLevel.mechanics.size}: ${activeMechanic?.title ?: ""}".uppercase(),
                                    color = ImmersiveSoftLilac,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Active Puzzle Mechanic Container
                if (activeMechanic != null) {
                    when (val data = activeMechanic.data) {
                        is MechanicData.Wires -> {
                            PuzzleWireCutting(
                                config = data.config,
                                isHintActive = gameState.isHintActive,
                                onWireCut = { idx, correct ->
                                    viewModel.onWireCut(idx, correct)
                                }
                            )
                        }
                        is MechanicData.Code -> {
                            PuzzleCodeEntry(
                                config = data.config,
                                isHintActive = gameState.isHintActive,
                                onCodeEntered = { correct ->
                                    viewModel.onCodeEntered(correct)
                                }
                            )
                        }
                        is MechanicData.Symbols -> {
                            PuzzleSymbolMatching(
                                config = data.config,
                                isHintActive = gameState.isHintActive,
                                onMatched = { correct ->
                                    viewModel.onSymbolMatched(correct)
                                }
                            )
                        }
                        is MechanicData.Sequence -> {
                            PuzzleButtonSequence(
                                config = data.config,
                                isHintActive = gameState.isHintActive,
                                onSequenceCompleted = { correct ->
                                    viewModel.onButtonSequenceCompleted(correct)
                                }
                            )
                        }
                        is MechanicData.Tapping -> {
                            PuzzleSpeedTapping(
                                config = data.config,
                                isHintActive = gameState.isHintActive,
                                onCompleted = { correct ->
                                    viewModel.onSpeedTappingCompleted(correct)
                                }
                            )
                        }
                        is MechanicData.Frequency -> {
                            PuzzleFrequencyTuning(
                                config = data.config,
                                isHintActive = gameState.isHintActive,
                                onCompleted = { correct ->
                                    viewModel.onFrequencyTuned(correct)
                                }
                            )
                        }
                        is MechanicData.Polarity -> {
                            PuzzlePolarityMatching(
                                config = data.config,
                                isHintActive = gameState.isHintActive,
                                onCompleted = { correct ->
                                    viewModel.onPolarityCompleted(correct)
                                }
                            )
                        }
                        is MechanicData.Arithmetic -> {
                            PuzzleArithmetic(
                                config = data.config,
                                isHintActive = gameState.isHintActive,
                                onCompleted = { correct ->
                                    viewModel.onArithmeticAnswered(correct)
                                }
                            )
                        }
                        is MechanicData.Rotary -> {
                            PuzzleRotaryDial(
                                config = data.config,
                                isHintActive = gameState.isHintActive,
                                onCompleted = { correct ->
                                    viewModel.onRotaryDialCompleted(correct)
                                }
                            )
                        }
                        is MechanicData.Fuses -> {
                            PuzzleCircuitFuses(
                                config = data.config,
                                isHintActive = gameState.isHintActive,
                                onCompleted = { correct ->
                                    viewModel.onCircuitFusesCompleted(correct)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Mission Briefing Dialog (Blocking modal dialog - user can only click Proceed with defusal)
            if (gameState.showTutorialOverlay) {
                val instruction = currentLevel.tutorialClue
                    ?: "Inspect the explosive ordnance telemetry and neutralize all ${currentLevel.mechanics.size} component(s) before the countdown reaches zero."
                TutorialOverlay(
                    missionTitle = currentLevel.title,
                    stepInstruction = instruction,
                    timeLimitSeconds = currentLevel.timeLimitSeconds,
                    mechanicsCount = currentLevel.mechanics.size,
                    onDismiss = { viewModel.dismissTutorialAndStart() }
                )
            }
        }
    }

    // Dialogs
    if (gameState.isPaused) {
        PauseDialog(
            soundEnabled = userGameData.soundEnabled,
            onToggleSound = { viewModel.toggleSound() },
            onResume = { viewModel.resumeGame() },
            onRestart = { viewModel.restartCurrentLevel() },
            onQuitToMenu = onBackToMenu
        )
    }

    if (gameState.isLevelComplete) {
        LevelCompleteDialog(
            levelNumber = currentLevel.levelNumber,
            starsEarned = gameState.starsEarned,
            secondsLeft = gameState.secondsRemaining,
            score = gameState.calculatedScore,
            hasNextLevel = true,
            onNextLevel = {
                viewModel.loadLevel(currentLevel.levelNumber + 1)
            },
            onReplay = { viewModel.restartCurrentLevel() },
            onLevelSelect = onLevelSelect
        )
    }

    if (gameState.isGameOver) {
        GameOverDialog(
            reasonMessage = gameState.gameOverReason,
            livesLeft = userGameData.lives,
            nextLifeRemainingMs = remainingRefillMs,
            onRetry = {
                if (userGameData.lives > 0) {
                    viewModel.restartCurrentLevel()
                }
            },
            onWatchAdForLife = {
                if (activity != null) {
                    viewModel.watchAdForLife(activity)
                }
            },
            onOpenStore = { viewModel.openStore() },
            onQuitToMenu = onBackToMenu
        )
    }

    if (gameState.showStoreDialog) {
        StoreDialog(
            onDismiss = { viewModel.closeStore() },
            onSimulatePurchase = { item ->
                viewModel.simulateStorePurchase(item)
                scope.launch {
                    snackbarHostState.showSnackbar("Purchased $item! (Simulated)")
                }
            }
        )
    }
}
