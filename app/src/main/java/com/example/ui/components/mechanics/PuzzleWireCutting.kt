package com.example.ui.components.mechanics

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.WireCuttingConfig
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveSoftLilac

@Composable
fun PuzzleWireCutting(
    config: WireCuttingConfig,
    isHintActive: Boolean,
    onWireCut: (wireIndex: Int, isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Track cut state of individual wires
    val cutState = remember(config) {
        mutableStateMapOf<Int, Boolean>().apply {
            config.wires.indices.forEach { put(it, false) }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "hint_glow")
    val hintPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hint_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ImmersiveCard, RoundedCornerShape(24.dp))
            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(24.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Clue panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF211F24), RoundedCornerShape(16.dp))
                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LOGIC INTEL: ${config.clueText}".uppercase(),
                color = ImmersiveLavender,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }

        // Tactical Hint clarification in Sector 2+
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

        // Wire Rack Canvas & Interactive Columns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            config.wires.forEachIndexed { index, wire ->
                val isCut = cutState[index] == true
                val isCorrect = index == config.correctWireIndex
                // Only direct highlight during tutorial sector
                val isTutorialDirect = config.hintClarification.startsWith("Direct clue:")
                val isHighlighted = isHintActive && isCorrect && isTutorialDirect

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("wire_column_$index")
                        .clickable(enabled = !isCut) {
                            cutState[index] = true
                            onWireCut(index, isCorrect)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .width(44.dp)
                            .height(125.dp)
                            .scale(if (isHighlighted) hintPulse else 1f)
                            .then(
                                if (isHighlighted) {
                                    Modifier.border(2.dp, ImmersiveLavender, RoundedCornerShape(8.dp))
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawWire(
                                wireColor = Color(wire.colorHex),
                                isCut = isCut,
                                isStriped = wire.isStriped
                            )
                        }

                        if (!isCut) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = "Cut Wire ${wire.colorName}",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .padding(bottom = 6.dp)
                                    .scale(0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isCut) "CUT" else wire.colorName,
                        color = if (isCut) Color(0xFF64748B) else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawWire(
    wireColor: Color,
    isCut: Boolean,
    isStriped: Boolean
) {
    val w = size.width
    val h = size.height
    val midX = w / 2f

    // Top and bottom terminal connectors (metal terminals)
    drawRoundRect(
        color = Color(0xFF94A3B8),
        topLeft = Offset(midX - 10f, 0f),
        size = androidx.compose.ui.geometry.Size(20f, 16f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color(0xFF94A3B8),
        topLeft = Offset(midX - 10f, h - 16f),
        size = androidx.compose.ui.geometry.Size(20f, 16f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
    )

    if (!isCut) {
        // Continuous wire path with slight natural bow
        val wirePath = Path().apply {
            moveTo(midX, 14f)
            cubicTo(
                midX - 8f, h * 0.35f,
                midX + 8f, h * 0.65f,
                midX, h - 14f
            )
        }

        // Shadow/depth
        drawPath(
            path = wirePath,
            color = Color.Black.copy(alpha = 0.3f),
            style = Stroke(width = 14f, cap = StrokeCap.Round)
        )

        // Wire core
        drawPath(
            path = wirePath,
            color = wireColor,
            style = Stroke(
                width = 10f,
                cap = StrokeCap.Round,
                pathEffect = if (isStriped) PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f) else null
            )
        )

        // Specular line
        drawPath(
            path = wirePath,
            color = Color.White.copy(alpha = 0.35f),
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )
    } else {
        // Severed Wire - top half snapping up-left
        val topSeveredPath = Path().apply {
            moveTo(midX, 14f)
            cubicTo(midX - 6f, h * 0.25f, midX - 14f, h * 0.38f, midX - 16f, h * 0.42f)
        }
        drawPath(
            path = topSeveredPath,
            color = wireColor,
            style = Stroke(width = 10f, cap = StrokeCap.Round)
        )
        // Copper core exposed tip
        drawCircle(
            color = Color(0xFFEA580C),
            radius = 3.5f,
            center = Offset(midX - 16f, h * 0.42f)
        )

        // Severed Wire - bottom half snapping down-right
        val bottomSeveredPath = Path().apply {
            moveTo(midX, h - 14f)
            cubicTo(midX + 6f, h * 0.75f, midX + 14f, h * 0.62f, midX + 16f, h * 0.58f)
        }
        drawPath(
            path = bottomSeveredPath,
            color = wireColor,
            style = Stroke(width = 10f, cap = StrokeCap.Round)
        )
        drawCircle(
            color = Color(0xFFEA580C),
            radius = 3.5f,
            center = Offset(midX + 16f, h * 0.58f)
        )

        // Spark particle near the cut gap
        drawCircle(
            color = Color(0xFFFDE047),
            radius = 3f,
            center = Offset(midX, h * 0.5f)
        )
    }
}
