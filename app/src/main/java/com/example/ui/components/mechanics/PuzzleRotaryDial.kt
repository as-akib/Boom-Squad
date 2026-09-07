package com.example.ui.components.mechanics

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.RotaryDialConfig
import com.example.ui.theme.ArcadeBg
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.ArcadeCardBorder
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PunchyOrange
import kotlin.math.abs

@Composable
fun PuzzleRotaryDial(
    config: RotaryDialConfig,
    isHintActive: Boolean,
    onCompleted: (isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var dialValue by remember(config) { mutableFloatStateOf(50f) }
    var isErrorFlash by remember(config) { mutableStateOf(false) }

    val currentInt = dialValue.toInt()
    val isAligned = abs(currentInt - config.targetValue) <= config.tolerance

    val gaugeBorderColor by animateColorAsState(
        targetValue = when {
            isErrorFlash -> DangerRed
            isAligned -> NeonLime
            else -> ElectricCyan
        },
        animationSpec = tween(durationMillis = 200),
        label = "gauge_border"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ArcadeCard, RoundedCornerShape(24.dp))
            .border(1.dp, ArcadeCardBorder, RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Clue Plate
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ArcadeBg, RoundedCornerShape(12.dp))
                .border(1.dp, ArcadeCardBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CALIBRATION CLUE",
                    color = BrightYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = config.clueText,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                if (isHintActive) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TACTICAL HINT: ${config.hintClarification}",
                        color = BrightYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rotary Dial Tumbler Graphic Display
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(130.dp)
                .shadow(elevation = 8.dp, shape = CircleShape, spotColor = gaugeBorderColor.copy(alpha = 0.5f))
                .background(ArcadeBg, CircleShape)
                .border(3.dp, gaugeBorderColor, CircleShape)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = config.dialLabel,
                    color = ElectricCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = String.format("%02d", currentInt),
                    color = if (isAligned) NeonLime else Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (isAligned) "LOCK ENGAGED" else "ROTATING",
                    color = if (isAligned) NeonLime else BrightYellow,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Step buttons (-10, -1, +1, +10)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(-10, -1, 1, 10).forEach { delta ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ArcadeBg)
                        .border(1.dp, ArcadeCardBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            dialValue = (dialValue + delta).coerceIn(0f, 99f)
                        }
                        .testTag("rotary_step_${if (delta > 0) "plus" else "minus"}_${abs(delta)}")
                ) {
                    Text(
                        text = if (delta > 0) "+$delta" else "$delta",
                        color = ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Smooth Slider
        Slider(
            value = dialValue,
            onValueChange = { dialValue = it },
            valueRange = 0f..99f,
            colors = SliderDefaults.colors(
                thumbColor = if (isAligned) NeonLime else PunchyOrange,
                activeTrackColor = ElectricCyan,
                inactiveTrackColor = ArcadeBg
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("rotary_dial_slider")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Submit Button
        Button(
            onClick = {
                if (isAligned) {
                    onCompleted(true)
                } else {
                    isErrorFlash = true
                    onCompleted(false)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAligned) NeonLime else PunchyOrange,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("rotary_dial_submit_button")
        ) {
            Text(
                text = "ENGAGE TUMBLER LOCK",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}
