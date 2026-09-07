package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ImmersiveCaption
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMuted
import com.example.ui.theme.ImmersiveSoftLilac
import com.example.ui.theme.SuccessGreen

@Composable
fun LevelCompleteDialog(
    levelNumber: Int,
    starsEarned: Int,
    secondsLeft: Int,
    score: Int,
    hasNextLevel: Boolean,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onLevelSelect: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(32.dp))
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = ImmersiveLavender.copy(alpha = 0.3f)
                )
                .testTag("level_complete_dialog")
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ImmersiveLavender.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = androidx.compose.ui.geometry.Offset(300f, 50f),
                            radius = 400f
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SECTOR NEUTRALIZED!",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = "Sector $levelNumber Cleared Successfully",
                    color = ImmersiveLavender,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Defused smiling bomb mini-portrait
                CartoonBombView(
                    size = 96.dp,
                    mood = BombMood.DEFUSED
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stars display (1 to 3) in Immersive Lavender
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        val isLit = i <= starsEarned
                        Icon(
                            imageVector = if (isLit) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star $i",
                            tint = if (isLit) ImmersiveLavender else ImmersiveCardBorder,
                            modifier = Modifier
                                .size(36.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Box (Time left & Score)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF211F24), RoundedCornerShape(20.dp))
                        .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(20.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TIME BUFFER", color = ImmersiveCaption, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("${secondsLeft}s", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SCORE", color = ImmersiveCaption, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("$score pts", color = ImmersiveLavender, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (hasNextLevel) {
                    Button(
                        onClick = onNextLevel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ImmersiveLavender,
                            contentColor = ImmersiveDarkPurple
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                spotColor = ImmersiveLavender.copy(alpha = 0.4f)
                            )
                            .testTag("complete_next_level_button")
                    ) {
                        Text("NEXT MISSION", fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = ImmersiveDarkPurple)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ImmersiveCard,
                            contentColor = ImmersiveSoftLilac
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("complete_replay_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = ImmersiveSoftLilac, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RETRY", color = ImmersiveSoftLilac, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onLevelSelect,
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ImmersiveCard,
                            contentColor = ImmersiveSoftLilac
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("complete_levels_button")
                    ) {
                        Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = ImmersiveSoftLilac, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SECTORS", color = ImmersiveSoftLilac, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
