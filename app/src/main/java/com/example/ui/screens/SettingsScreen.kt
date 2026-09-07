package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelRepository
import com.example.data.models.SectorNavigationUtils
import com.example.ui.theme.DangerRed
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

@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    onBackClick: () -> Unit
) {
    val userGameData by viewModel.userGameData.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ImmersiveBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Immersive Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ImmersiveCard)
                        .border(1.dp, ImmersiveCardBorder, CircleShape)
                        .clickable { onBackClick() }
                        .testTag("settings_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ImmersiveSoftLilac,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "SETTINGS & MANUAL",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-0.5).sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Audio & Haptics Preferences Card (rounded-3xl, #2B2930, border #49454F)
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "FEEDBACK & CONTROLS",
                        color = ImmersiveLavender,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SettingToggleRow(
                        icon = Icons.Default.VolumeUp,
                        title = "Audio Effects",
                        subtitle = "Tactical audio cues, wire snips, and ticking",
                        checked = userGameData.soundEnabled,
                        onCheckedChange = { viewModel.toggleSound() },
                        testTag = "toggle_sound_switch"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SettingToggleRow(
                        icon = Icons.Default.Vibration,
                        title = "Haptic Vibration",
                        subtitle = "Sensory feedback on snips, keypad inputs and detonation",
                        checked = userGameData.hapticsEnabled,
                        onCheckedChange = { viewModel.toggleHaptics() },
                        testTag = "toggle_haptics_switch"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field Manual / How to play
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "TACTICAL FIELD MANUAL",
                        color = ImmersiveLavender,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ManualItem(
                        title = "1. Wire Cutting Protocol",
                        description = "Inspect the ordnance condition clue at the top. Snip the exact wire fulfilling the rule before the timer expires. Incorrect cuts trigger immediate detonation!"
                    )

                    ManualItem(
                        title = "2. Keypad Code Entry",
                        description = "Decrypt the mathematical, sequence, or symbolic keypad clue and punch the exact sequence of digits into the digital terminal."
                    )

                    ManualItem(
                        title = "3. Symbol Glyph Matching",
                        description = "Analyze the esoteric glyphs and tap them in the exact sequential order indicated by the sector's cipher column key."
                    )

                    ManualItem(
                        title = "4. Button Sequence Matrix",
                        description = "Engage illuminated push-buttons in the designated priority pattern without hesitation to disarm the circuit."
                    )

                    ManualItem(
                        title = "5. Rapid Defusal Tapping",
                        description = "Repeatedly tap the high-pressure override capacitor rapidly before time runs out to vent dangerous explosive buildup."
                    )

                    ManualItem(
                        title = "6. Frequency Radio Tuner",
                        description = "Adjust the frequency slider to locate the harmonic resonance wave where static dissolves into clean signal."
                    )

                    ManualItem(
                        title = "7. Polarity Toggle Relays",
                        description = "Configure binary relays to balance charge flows (+/-) across the grid as dictated by electrical schematics."
                    )

                    ManualItem(
                        title = "8. Mental Arithmetic Bypass",
                        description = "Calculate the explosive yield logic arithmetic equation and enter the correct mathematical sum before countdown detonation."
                    )

                    ManualItem(
                        title = "9. Rotary Lock Dial",
                        description = "Rotate the calibrated rotary dial in sequence (Clockwise/Counter-Clockwise) matching the classified safe combination stops."
                    )

                    ManualItem(
                        title = "10. Circuit Fuse Replacement",
                        description = "Inspect amperage requirements on blown fuse sockets and install matching rating fuses to safely bridge power lines."
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Data & Agent Records Card
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "DATA & AGENT RECORDS",
                        color = DangerRed,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val (curSector, curLvlInSec) = SectorNavigationUtils.getSectorAndLevel(userGameData.unlockedLevel)
                    val sectorInfo = LevelRepository.getSectorInfo(curSector)
                    val totalStars = userGameData.levelStars.values.sum()
                    val totalCompletedLevels = userGameData.levelStars.size

                    // Grid of key Agent Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Current Sector Card
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF211F24), RoundedCornerShape(16.dp))
                                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Public, contentDescription = null, tint = ImmersiveLavender, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ASSIGNED SECTOR", color = ImmersiveCaption, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (curSector == 1) "SECTOR 1: TRAINING" else "SECTOR $curSector: ${sectorInfo.name.removePrefix("SECTOR $curSector: ")}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1
                            )
                            Text(
                                text = "Sector Lvl $curLvlInSec • Global Lvl ${userGameData.unlockedLevel}",
                                color = ImmersiveSoftLilac,
                                fontSize = 10.sp
                            )
                        }

                        // Total Stars Card
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF211F24), RoundedCornerShape(16.dp))
                                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Diamond, contentDescription = null, tint = ImmersiveLavender, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("HONOR STARS", color = ImmersiveCaption, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalStars Stars",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "$totalCompletedLevels defused missions",
                                color = ImmersiveSoftLilac,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary record row: Clearance Rank & Energy units
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Clearance Rank
                        val rankName = when {
                            curSector >= 10 -> "LEGENDARY DEFUSER"
                            curSector >= 7 -> "SPECIAL OPERATIVE"
                            curSector >= 4 -> "TACTICAL VETERAN"
                            curSector >= 2 -> "FIELD OPERATIVE"
                            else -> "NOVICE RECRUIT"
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF211F24), RoundedCornerShape(16.dp))
                                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = ImmersiveLavender, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CLEARANCE RANK", color = ImmersiveCaption, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rankName,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Grade Level: $curSector",
                                color = ImmersiveSoftLilac,
                                fontSize = 10.sp
                            )
                        }

                        // Energy Reserves
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF211F24), RoundedCornerShape(16.dp))
                                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ENERGY STATUS", color = ImmersiveCaption, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${userGameData.lives}/5 Units",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (userGameData.lives >= 5) "Fully Charged" else "Recharging",
                                color = ImmersiveSoftLilac,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("reset_progress_button")
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = DangerRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PURGE AGENT PROGRESS", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bomb Squad • Quantum Immersion Edition",
                color = ImmersiveCaption,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            containerColor = ImmersiveCard,
            onDismissRequest = { showResetDialog = false },
            title = { Text("Purge Mission Data?", fontWeight = FontWeight.Black, color = Color.White) },
            text = { Text("This will reset all unlocked sectors, stars, and records back to Level 1. This action is irreversible.", color = ImmersiveMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White),
                    shape = CircleShape
                ) {
                    Text("PURGE DATA", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("CANCEL", color = ImmersiveSoftLilac)
                }
            }
        )
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ImmersiveMidPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = ImmersiveSoftLilac, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = ImmersiveCaption, fontSize = 11.sp)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ImmersiveDarkPurple,
                checkedTrackColor = ImmersiveLavender,
                uncheckedThumbColor = ImmersiveCaption,
                uncheckedTrackColor = ImmersiveCardBorder
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun ManualItem(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(title, color = ImmersiveSoftLilac, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(description, color = ImmersiveMuted, fontSize = 11.sp, lineHeight = 16.sp)
    }
}
