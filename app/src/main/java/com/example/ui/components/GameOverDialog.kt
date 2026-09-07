package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ImmersiveCaption
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMuted
import com.example.ui.theme.ImmersiveSoftLilac

@Composable
fun GameOverDialog(
    reasonMessage: String,
    livesLeft: Int,
    nextLifeRemainingMs: Long,
    onRetry: () -> Unit,
    onWatchAdForLife: () -> Unit,
    onOpenStore: () -> Unit,
    onQuitToMenu: () -> Unit
) {
    val isOutOfLives = livesLeft <= 0

    val refillMin = (nextLifeRemainingMs / 1000L / 60L).toInt()
    val refillSec = ((nextLifeRemainingMs / 1000L) % 60L).toInt()
    val timerFormatted = String.format("%02d:%02d", refillMin, refillSec)

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DangerRed.copy(alpha = 0.6f), RoundedCornerShape(32.dp))
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = DangerRed.copy(alpha = 0.3f)
                )
                .testTag("game_over_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isOutOfLives) "OUT OF ENERGY!" else "BOOM! DETONATED!",
                    color = DangerRed,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = reasonMessage,
                    color = ImmersiveMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Cartoon Exploded Bomb with soot
                CartoonBombView(
                    size = 100.dp,
                    mood = BombMood.EXPLODED
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (isOutOfLives) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF211F24), RoundedCornerShape(20.dp))
                            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(20.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Next energy refill in:",
                                color = ImmersiveCaption,
                                fontSize = 11.sp
                            )
                            Text(
                                text = timerFormatted,
                                color = ImmersiveSoftLilac,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Watch ad for life button
                    Button(
                        onClick = onWatchAdForLife,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ImmersiveLavender,
                            contentColor = ImmersiveDarkPurple
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("watch_ad_for_life_button")
                    ) {
                        Icon(Icons.Default.OndemandVideo, contentDescription = null, tint = ImmersiveDarkPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WATCH INTEL AD (+1 ENERGY)", color = ImmersiveDarkPurple, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Store placeholder
                    OutlinedButton(
                        onClick = onOpenStore,
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ImmersiveCard,
                            contentColor = ImmersiveSoftLilac
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("open_store_button")
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = ImmersiveLavender, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("BUY ENERGY PACK", color = ImmersiveSoftLilac, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    // Still has lives
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF211F24), CircleShape)
                            .border(1.dp, ImmersiveCardBorder, CircleShape)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Energy Remaining: $livesLeft / 5",
                            color = ImmersiveSoftLilac,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ImmersiveLavender,
                            contentColor = ImmersiveDarkPurple
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                spotColor = ImmersiveLavender.copy(alpha = 0.3f)
                            )
                            .testTag("game_over_retry_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = ImmersiveDarkPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RETRY MISSION", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onQuitToMenu,
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveCardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = ImmersiveCard,
                        contentColor = ImmersiveSoftLilac
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("game_over_menu_button")
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = ImmersiveSoftLilac, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ABORT TO HQ", color = ImmersiveSoftLilac, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
