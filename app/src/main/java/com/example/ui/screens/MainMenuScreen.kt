package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.shadow
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
import com.example.data.models.SectorNavigationUtils
import com.example.ui.components.BombMood
import com.example.ui.components.CartoonBombView
import com.example.ui.components.LivesHud
import com.example.ui.components.StoreDialog
import com.example.ui.theme.ArcadeBg
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.ArcadeCardBorder
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.ElectricCyan
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
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PunchyOrange
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    onPlayClick: (levelNumber: Int) -> Unit,
    onLevelSelectClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val userGameData by viewModel.userGameData.collectAsState()
    val gameplayState by viewModel.gameplayState.collectAsState()

    val (_, remainingRefillMs) = userGameData.getEffectiveLives(System.currentTimeMillis())
    val totalStars = userGameData.levelStars.values.sum()

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
                        center = androidx.compose.ui.geometry.Offset(800f, 100f),
                        radius = 900f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 500.dp)
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Immersive UI Header: Profile / Status & Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left profile pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(ImmersiveLavender, ImmersiveDarkPurple)
                                    )
                                )
                                .border(2.dp, ImmersiveMidPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Agent Badge",
                                tint = ImmersiveDarkPurple,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            val (currentSector, levelInSector) = SectorNavigationUtils.getSectorAndLevel(userGameData.unlockedLevel)
                            val currentSectorInfo = LevelRepository.getSectorInfo(currentSector)
                            val sectorDisplayName = if (currentSector == 1) "TRAINING FACILITY" else currentSectorInfo.name.removePrefix("SECTOR $currentSector: ")
                            
                            Text(
                                text = "SECTOR $currentSector: $sectorDisplayName".uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = ImmersiveLavender,
                                maxLines = 1
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val sectorTotal = SectorNavigationUtils.getLevelsCountForSector(currentSector)
                                Text(
                                    text = if (currentSector == 1) "TUTORIAL $levelInSector/$sectorTotal" else "SECTOR $currentSector • LEVEL $levelInSector",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ImmersiveText
                                )
                                // Sector Progress bar
                                val progressFraction = (levelInSector / sectorTotal.toFloat()).coerceIn(0.05f, 1f)
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(ImmersiveCardBorder)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(progressFraction)
                                            .height(6.dp)
                                            .background(ImmersiveLavender)
                                    )
                                }
                            }
                        }
                    }

                    // Header Right: Store & Settings
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ImmersiveCard)
                                .border(1.dp, ImmersiveCardBorder, CircleShape)
                                .clickable { viewModel.openStore() }
                                .testTag("menu_store_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Store",
                                tint = ImmersiveSoftLilac,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ImmersiveCard)
                                .border(1.dp, ImmersiveCardBorder, CircleShape)
                                .clickable { onSettingsClick() }
                                .testTag("menu_settings_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = ImmersiveSoftLilac,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Center Immersive Hero Card with Bomb Visual & Radial Glow
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(ArcadeCard)
                        .border(
                            width = 2.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(ElectricCyan, CyberPurple, PunchyOrange)
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ElectricCyan.copy(alpha = 0.18f),
                                    CyberPurple.copy(alpha = 0.08f),
                                    Color.Transparent
                                ),
                                center = androidx.compose.ui.geometry.Offset(500f, 80f),
                                radius = 600f
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Title
                        Text(
                            text = "QUANTUM DEFUSE",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = (-0.5).sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Neutralize the ordnance before detonation.",
                            color = ImmersiveMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // Hero Cartoon Bomb Character
                        CartoonBombView(
                            size = 145.dp,
                            mood = BombMood.NORMAL
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Colorful Punchy Action Button: Enter Mission
                        Button(
                            onClick = {
                                if (userGameData.lives > 0) {
                                    onPlayClick(userGameData.unlockedLevel)
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Out of lives! Wait for refill or watch an ad.")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PunchyOrange,
                                contentColor = Color.White
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(54.dp)
                                .shadow(
                                    elevation = 18.dp,
                                    shape = CircleShape,
                                    spotColor = PunchyOrange.copy(alpha = 0.6f),
                                    ambientColor = PunchyOrange.copy(alpha = 0.4f)
                                )
                                .padding(horizontal = 8.dp)
                                .testTag("menu_play_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                val (curSec, curLvl) = SectorNavigationUtils.getSectorAndLevel(userGameData.unlockedLevel)
                                val curSecInfo = LevelRepository.getSectorInfo(curSec)
                                val buttonText = if (curSec == 1) "START TUTORIAL $curLvl" else "ENTER ${curSecInfo.name} • LVL $curLvl"
                                Text(
                                    text = buttonText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Immersive UI Metric Cards Grid (Energy/Lives & Stars/Credits)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Energy / Lives Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(105.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(ArcadeCard)
                            .border(1.5.dp, BrightYellow.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                            .clickable {
                                if (activity != null) {
                                    viewModel.watchAdForLife(activity)
                                }
                            }
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Energy",
                                    tint = BrightYellow,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "ENERGY",
                                    color = BrightYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Column {
                                Text(
                                    text = "${userGameData.lives}/5",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                val refillMin = (remainingRefillMs / 1000L / 60L).toInt()
                                val refillSec = ((remainingRefillMs / 1000L) % 60L).toInt()
                                val caption = if (userGameData.lives < 5) "Refills in ${refillMin}m ${refillSec}s" else "Full Capacity"
                                Text(
                                    text = caption,
                                    color = ImmersiveCaption,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Credits / Stars Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(105.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(ArcadeCard)
                            .border(1.5.dp, NeonLime.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                            .clickable { onLevelSelectClick() }
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = "Stars",
                                    tint = NeonLime,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "STARS",
                                    color = NeonLime,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Column {
                                val (curSec, _) = SectorNavigationUtils.getSectorAndLevel(userGameData.unlockedLevel)
                                Text(
                                    text = "$totalStars",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Reached Sector $curSec",
                                    color = ImmersiveCaption,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (gameplayState.showStoreDialog) {
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
