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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMidPurple
import com.example.ui.theme.ImmersiveSoftLilac

@Composable
fun PauseDialog(
    soundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuitToMenu: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
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
                .testTag("pause_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MISSION SUSPENDED",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sound toggle quick button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(Color(0xFF211F24), CircleShape)
                        .border(1.dp, ImmersiveCardBorder, CircleShape)
                        .padding(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Text(text = "Tactical Audio:", color = ImmersiveSoftLilac, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onToggleSound) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeDown else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Sound",
                            tint = if (soundEnabled) ImmersiveLavender else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Resume button
                Button(
                    onClick = onResume,
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
                        .testTag("pause_resume_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ImmersiveDarkPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RESUME MISSION", fontWeight = FontWeight.Black, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Restart button
                OutlinedButton(
                    onClick = onRestart,
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveCardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = ImmersiveCard,
                        contentColor = ImmersiveSoftLilac
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("pause_restart_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = ImmersiveSoftLilac, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RESTART SECTOR", color = ImmersiveSoftLilac, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quit to menu
                OutlinedButton(
                    onClick = onQuitToMenu,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = ImmersiveCard,
                        contentColor = DangerRed
                    ),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("pause_quit_button")
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ABORT TO HQ", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
