package com.example.ui.components.mechanics

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.SpeedTappingConfig
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMidPurple
import com.example.ui.theme.ImmersiveSoftLilac
import com.example.ui.theme.SuccessGreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PuzzleSpeedTapping(
    config: SpeedTappingConfig,
    isHintActive: Boolean,
    onCompleted: (isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var stableTapsCount by remember(config) { mutableIntStateOf(0) }
    var isErrorFlash by remember(config) { mutableStateOf(false) }

    // Needle oscillation animation between 0f and 1f
    val infiniteTransition = rememberInfiniteTransition(label = "pressure_osc")
    val needlePosition by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "needle_val"
    )

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
                text = "HYDRAULIC INTEL: ${config.clueText}".uppercase(),
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

        Spacer(modifier = Modifier.height(10.dp))

        // Progress indicators (required taps)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until config.tapsRequired) {
                val isDone = i < stableTapsCount
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .padding(horizontal = 3.dp)
                        .clip(CircleShape)
                        .background(
                            if (isErrorFlash) DangerRed
                            else if (isDone) SuccessGreen
                            else Color(0xFF211F24)
                        )
                        .border(
                            1.dp,
                            if (isDone) SuccessGreen else ImmersiveCardBorder,
                            CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pressure Gauge Meter Canvas
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(Color(0xFF1E1D22), CircleShape)
                .border(2.dp, ImmersiveCardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f

                // Arc background
                drawArc(
                    color = Color(0xFF333038),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )

                // Safe zone arc
                val startSweep = 135f + (config.targetMinPressure * 270f)
                val safeSweep = (config.targetMaxPressure - config.targetMinPressure) * 270f
                drawArc(
                    color = if (isHintActive) SuccessGreen else ImmersiveLavender.copy(alpha = 0.8f),
                    startAngle = startSweep,
                    sweepAngle = safeSweep,
                    useCenter = false,
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )

                // Needle
                val needleAngle = 135f + (needlePosition * 270f)
                val rad = Math.toRadians(needleAngle.toDouble())
                val needleEnd = Offset(
                    x = center.x + (radius * 0.75f * cos(rad)).toFloat(),
                    y = center.y + (radius * 0.75f * sin(rad)).toFloat()
                )

                drawLine(
                    color = if (isErrorFlash) DangerRed else Color.White,
                    start = center,
                    end = needleEnd,
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = ImmersiveLavender,
                    radius = 10f,
                    center = center
                )
            }

            // Central PSI readout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 50.dp)
            ) {
                val currentPsi = (needlePosition * 100).toInt()
                Text(
                    text = "$currentPsi PSI",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                if (config.dialIndirectClue.isNotBlank()) {
                    Text(
                        text = config.dialIndirectClue,
                        color = ImmersiveLavender,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stabilize Action Button
        Button(
            onClick = {
                isErrorFlash = false
                val inRange = needlePosition in config.targetMinPressure..config.targetMaxPressure
                if (inRange) {
                    val nextCount = stableTapsCount + 1
                    stableTapsCount = nextCount
                    if (nextCount >= config.tapsRequired) {
                        onCompleted(true)
                    }
                } else {
                    isErrorFlash = true
                    onCompleted(false)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ImmersiveMidPurple,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(52.dp)
                .border(1.dp, ImmersiveLavender, RoundedCornerShape(16.dp))
                .testTag("stabilize_pressure_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Compress,
                    contentDescription = null,
                    tint = ImmersiveLavender,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "STABILIZE PRESSURE (${stableTapsCount}/${config.tapsRequired})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
