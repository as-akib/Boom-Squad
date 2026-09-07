package com.example.ui.components.mechanics

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.FrequencyTuningConfig
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMidPurple
import com.example.ui.theme.ImmersiveSoftLilac
import com.example.ui.theme.SuccessGreen
import kotlin.math.abs

@Composable
fun PuzzleFrequencyTuning(
    config: FrequencyTuningConfig,
    isHintActive: Boolean,
    onCompleted: (isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val initialFreq = ((config.minFrequencyKHz + config.maxFrequencyKHz) / 2).toFloat()
    var currentFreq by remember(config) { mutableFloatStateOf(initialFreq) }
    var isErrorFlash by remember(config) { mutableStateOf(false) }

    val currentInt = currentFreq.toInt()
    val diff = abs(currentInt - config.targetFrequencyKHz)
    val isLocked = diff <= config.toleranceKHz

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ImmersiveCard, RoundedCornerShape(24.dp))
            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(24.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Clue plate
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF211F24), RoundedCornerShape(16.dp))
                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "RADIO HARMONICS: ${config.clueText}".uppercase(),
                color = ImmersiveLavender,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }

        // Tactical Clue Hint clarification if active
        if (isHintActive && config.hintClarification.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ImmersiveDarkPurple, RoundedCornerShape(12.dp))
                    .border(1.dp, ImmersiveLavender, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TACTICAL HINT: ${config.hintClarification}",
                    color = ImmersiveSoftLilac,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large Frequency Display Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF19181C))
                .border(
                    1.dp,
                    if (isErrorFlash) DangerRed
                    else if (isLocked) SuccessGreen
                    else ImmersiveCardBorder,
                    RoundedCornerShape(18.dp)
                )
                .padding(vertical = 14.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = if (isLocked) SuccessGreen else ImmersiveLavender,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = config.stationCallsign,
                        color = ImmersiveLavender,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$currentInt kHz",
                    color = if (isLocked) SuccessGreen else Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = if (isLocked) "SIGNAL RESONANCE ACQUIRED" else "SEEKING CARRIER WAVE...",
                    color = if (isLocked) SuccessGreen else Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Slider control with fine-tune buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    val next = (currentFreq - 1).coerceAtLeast(config.minFrequencyKHz.toFloat())
                    currentFreq = next
                    isErrorFlash = false
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2B2930))
                    .border(1.dp, ImmersiveCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Step down 1 kHz",
                    tint = ImmersiveLavender
                )
            }

            Slider(
                value = currentFreq,
                onValueChange = {
                    currentFreq = it
                    isErrorFlash = false
                },
                valueRange = config.minFrequencyKHz.toFloat()..config.maxFrequencyKHz.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = if (isLocked) SuccessGreen else ImmersiveLavender,
                    activeTrackColor = if (isLocked) SuccessGreen else ImmersiveLavender,
                    inactiveTrackColor = Color(0xFF333038)
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .testTag("frequency_slider")
            )

            IconButton(
                onClick = {
                    val next = (currentFreq + 1).coerceAtMost(config.maxFrequencyKHz.toFloat())
                    currentFreq = next
                    isErrorFlash = false
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2B2930))
                    .border(1.dp, ImmersiveCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Step up 1 kHz",
                    tint = ImmersiveLavender
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Engage / Lock Button
        Button(
            onClick = {
                if (isLocked) {
                    onCompleted(true)
                } else {
                    isErrorFlash = true
                    onCompleted(false)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLocked) SuccessGreen else ImmersiveMidPurple,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp)
                .border(
                    1.dp,
                    if (isLocked) SuccessGreen else ImmersiveLavender,
                    RoundedCornerShape(16.dp)
                )
                .testTag("lock_frequency_button")
        ) {
            Text(
                text = if (isLocked) "TRANSMIT FREQUENCY OVERRIDE" else "ENGAGE FREQUENCY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}
