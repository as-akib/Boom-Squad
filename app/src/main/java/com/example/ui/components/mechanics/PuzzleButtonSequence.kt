package com.example.ui.components.mechanics

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ButtonSequenceConfig
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMidPurple
import com.example.ui.theme.ImmersiveSoftLilac
import com.example.ui.theme.SuccessGreen

@Composable
fun PuzzleButtonSequence(
    config: ButtonSequenceConfig,
    isHintActive: Boolean,
    onSequenceCompleted: (isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val pressedButtonIds = remember(config) { mutableStateListOf<Int>() }
    var isErrorFlash by remember(config) { mutableStateOf(false) }

    val nextRequiredId = if (pressedButtonIds.size < config.correctSequence.size) {
        config.correctSequence[pressedButtonIds.size]
    } else null

    val infiniteTransition = rememberInfiniteTransition(label = "btn_hint")
    val hintPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
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
                text = "SEQUENCE: ${config.clueText}".uppercase(),
                color = ImmersiveLavender,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }

        if (config.isReversed) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4A154B))
                    .border(1.dp, ImmersiveLavender, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "⚠️ EXECUTE IN REVERSE ORDER",
                    color = ImmersiveSoftLilac,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Tactical Hint clarification if active
        if (isHintActive && config.hintClarification.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2E1A47), RoundedCornerShape(12.dp))
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

        Spacer(modifier = Modifier.height(12.dp))

        // LED Progress Bar
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            config.correctSequence.indices.forEach { index ->
                val isLit = index < pressedButtonIds.size
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .padding(horizontal = 3.dp)
                        .clip(CircleShape)
                        .background(
                            if (isErrorFlash) DangerRed
                            else if (isLit) ImmersiveLavender
                            else Color(0xFF211F24)
                        )
                        .border(
                            1.dp,
                            if (isLit) ImmersiveLavender else ImmersiveCardBorder,
                            CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Push Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            config.buttons.forEach { button ->
                val isTutorialDirect = config.hintClarification.startsWith("Direct clue:")
                val isNextHint = isHintActive && button.id == nextRequiredId && isTutorialDirect
                val btnColor = Color(button.colorHex)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .scale(if (isNextHint) hintPulse else 1f)
                        .testTag("sequence_button_${button.label}")
                        .clickable {
                            isErrorFlash = false
                            val expectedId = config.correctSequence[pressedButtonIds.size]
                            if (button.id == expectedId) {
                                pressedButtonIds.add(button.id)
                                if (pressedButtonIds.size == config.correctSequence.size) {
                                    onSequenceCompleted(true)
                                }
                            } else {
                                isErrorFlash = true
                                pressedButtonIds.clear()
                                onSequenceCompleted(false)
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(
                                elevation = if (isNextHint) 10.dp else 4.dp,
                                shape = CircleShape,
                                spotColor = if (isNextHint) ImmersiveLavender else btnColor.copy(alpha = 0.4f)
                            )
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(btnColor, btnColor.copy(alpha = 0.75f))
                                )
                            )
                            .border(
                                2.dp,
                                if (isNextHint) ImmersiveLavender else ImmersiveCardBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = button.label,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
