package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class BombMood {
    NORMAL,
    NERVOUS,
    DEFUSED,
    EXPLODED
}

@Composable
fun CartoonBombView(
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    mood: BombMood = BombMood.NORMAL,
    shakeIntensity: Float = 0f // 0f to 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bomb_fx")

    // Shake animation
    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (mood == BombMood.NERVOUS) 60 else 120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    // Spark flicker animation
    val sparkPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spark"
    )

    // Sweat drop bobbing
    val sweatBob by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweat"
    )

    // Eye blink or wander
    val eyeWander by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eye"
    )

    val currentShakeX = if (shakeIntensity > 0f || mood == BombMood.NERVOUS) shakeOffset * (shakeIntensity.coerceAtLeast(0.4f) * 12f) else 0f
    val currentShakeRotation = if (shakeIntensity > 0f || mood == BombMood.NERVOUS) shakeOffset * (shakeIntensity.coerceAtLeast(0.4f) * 4f) else 0f

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                translationX = currentShakeX
                rotationZ = currentShakeRotation
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            val centerX = w * 0.5f
            val centerY = h * 0.56f
            val bombRadius = w * 0.36f

            if (mood == BombMood.EXPLODED) {
                drawExplosionCloud(centerX, centerY, bombRadius)
                return@Canvas
            }

            // 1. Draw Fuse (Top Curvy Cord)
            drawFuse(centerX, centerY - bombRadius, sparkPulse, mood == BombMood.DEFUSED)

            // 2. Draw Brass Bomb Cap / Neck
            val neckWidth = bombRadius * 0.44f
            val neckHeight = bombRadius * 0.22f
            val neckTop = centerY - bombRadius - neckHeight * 0.7f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFEAB308), Color(0xFFCA8A04), Color(0xFF713F12))
                ),
                topLeft = Offset(centerX - neckWidth / 2f, neckTop),
                size = Size(neckWidth, neckHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )

            // 3. Draw Bomb Body with Spherical Radial Gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4B5563),
                        Color(0xFF2B323B),
                        Color(0xFF15191E)
                    ),
                    center = Offset(centerX - bombRadius * 0.35f, centerY - bombRadius * 0.35f),
                    radius = bombRadius * 1.3f
                ),
                radius = bombRadius,
                center = Offset(centerX, centerY)
            )

            // Bomb body subtle outline
            drawCircle(
                color = Color(0xFF0F1216),
                radius = bombRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 4f)
            )

            // Specular light arc highlight on top-left
            val highlightPath = Path().apply {
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        left = centerX - bombRadius * 0.82f,
                        top = centerY - bombRadius * 0.82f,
                        right = centerX + bombRadius * 0.82f,
                        bottom = centerY + bombRadius * 0.82f
                    ),
                    startAngleDegrees = 190f,
                    sweepAngleDegrees = 65f,
                    forceMoveTo = false
                )
            }
            drawPath(
                path = highlightPath,
                color = Color.White.copy(alpha = 0.35f),
                style = Stroke(width = 9f, cap = StrokeCap.Round)
            )

            // 4. Draw Cute Animated Expressive Eyes & Mouth
            drawFace(
                centerX = centerX,
                centerY = centerY,
                radius = bombRadius,
                mood = mood,
                eyeOffset = eyeWander,
                sweatBob = sweatBob
            )
        }
    }
}

private fun DrawScope.drawFuse(
    startX: Float,
    startY: Float,
    sparkPulse: Float,
    isDefused: Boolean
) {
    val fusePath = Path().apply {
        moveTo(startX, startY + 4f)
        cubicTo(
            startX + 18f, startY - 25f,
            startX + 40f, startY - 20f,
            startX + 35f, startY - 48f
        )
    }

    // Fuse cord
    drawPath(
        path = fusePath,
        color = Color(0xFFD97706),
        style = Stroke(width = 8f, cap = StrokeCap.Round)
    )
    drawPath(
        path = fusePath,
        color = Color(0xFF78350F),
        style = Stroke(width = 4f, cap = StrokeCap.Round)
    )

    val sparkTipX = startX + 35f
    val sparkTipY = startY - 48f

    if (!isDefused) {
        // Burning Spark Flame
        val sparkRadius = 10f * sparkPulse
        drawCircle(
            color = Color(0xFFF97316),
            radius = sparkRadius * 1.5f,
            center = Offset(sparkTipX, sparkTipY)
        )
        drawCircle(
            color = Color(0xFFFEF08A),
            radius = sparkRadius * 0.9f,
            center = Offset(sparkTipX, sparkTipY)
        )
        drawCircle(
            color = Color.White,
            radius = sparkRadius * 0.45f,
            center = Offset(sparkTipX, sparkTipY)
        )

        // Spark rays
        for (i in 0 until 6) {
            val angle = (i * 60f + sparkPulse * 30f) * (PI / 180f).toFloat()
            val rayLen = 14f * sparkPulse
            drawLine(
                color = Color(0xFFFBBF24),
                start = Offset(sparkTipX, sparkTipY),
                end = Offset(sparkTipX + cos(angle) * rayLen, sparkTipY + sin(angle) * rayLen),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        }
    } else {
        // Smoke wisps for defused fuse
        drawCircle(
            color = Color(0xFF64748B),
            radius = 5f,
            center = Offset(sparkTipX, sparkTipY)
        )
    }
}

private fun DrawScope.drawFace(
    centerX: Float,
    centerY: Float,
    radius: Float,
    mood: BombMood,
    eyeOffset: Float,
    sweatBob: Float
) {
    val eyeY = centerY - radius * 0.1f
    val eyeSpacing = radius * 0.38f
    val leftEyeX = centerX - eyeSpacing
    val rightEyeX = centerX + eyeSpacing
    val eyeRadius = radius * 0.22f

    when (mood) {
        BombMood.NORMAL, BombMood.NERVOUS -> {
            // Big white sclera
            drawCircle(Color.White, radius = eyeRadius, center = Offset(leftEyeX, eyeY))
            drawCircle(Color.White, radius = eyeRadius, center = Offset(rightEyeX, eyeY))

            // Eye outline
            drawCircle(Color(0xFF1E293B), radius = eyeRadius, center = Offset(leftEyeX, eyeY), style = Stroke(width = 3f))
            drawCircle(Color(0xFF1E293B), radius = eyeRadius, center = Offset(rightEyeX, eyeY), style = Stroke(width = 3f))

            // Pupils
            val pupilRadius = if (mood == BombMood.NERVOUS) eyeRadius * 0.35f else eyeRadius * 0.5f
            val pupilShift = if (mood == BombMood.NERVOUS) eyeOffset * 1.5f else eyeOffset

            drawCircle(Color(0xFF0F172A), radius = pupilRadius, center = Offset(leftEyeX + pupilShift, eyeY + pupilShift * 0.5f))
            drawCircle(Color(0xFF0F172A), radius = pupilRadius, center = Offset(rightEyeX + pupilShift, eyeY + pupilShift * 0.5f))

            // Eye gleam highlights
            drawCircle(Color.White, radius = pupilRadius * 0.4f, center = Offset(leftEyeX + pupilShift - 3f, eyeY - 3f))
            drawCircle(Color.White, radius = pupilRadius * 0.4f, center = Offset(rightEyeX + pupilShift - 3f, eyeY - 3f))

            // Mouth
            val mouthY = centerY + radius * 0.32f
            if (mood == BombMood.NERVOUS) {
                // Nervous wavy mouth
                val mouthPath = Path().apply {
                    moveTo(centerX - 24f, mouthY)
                    quadraticTo(centerX - 12f, mouthY - 8f, centerX, mouthY)
                    quadraticTo(centerX + 12f, mouthY + 8f, centerX + 24f, mouthY)
                }
                drawPath(mouthPath, color = Color.White, style = Stroke(width = 4f, cap = StrokeCap.Round))

                // Sweat drop on forehead
                val sweatX = rightEyeX + eyeRadius * 0.9f
                val sweatY = eyeY - eyeRadius * 0.8f + sweatBob
                drawSweatDrop(sweatX, sweatY)
            } else {
                // Friendly smile
                val smilePath = Path().apply {
                    moveTo(centerX - 20f, mouthY - 4f)
                    quadraticTo(centerX, mouthY + 12f, centerX + 20f, mouthY - 4f)
                }
                drawPath(smilePath, color = Color.White, style = Stroke(width = 4.5f, cap = StrokeCap.Round))

                // Cute cheeks
                drawCircle(Color(0xFFFF7A00).copy(alpha = 0.4f), radius = 8f, center = Offset(leftEyeX - eyeRadius * 0.7f, eyeY + eyeRadius * 0.7f))
                drawCircle(Color(0xFFFF7A00).copy(alpha = 0.4f), radius = 8f, center = Offset(rightEyeX + eyeRadius * 0.7f, eyeY + eyeRadius * 0.7f))
            }
        }
        BombMood.DEFUSED -> {
            // Happy closed arc eyes (smiling curves ^_^)
            val leftEyeArc = Path().apply {
                moveTo(leftEyeX - eyeRadius, eyeY)
                quadraticTo(leftEyeX, eyeY - eyeRadius * 1.1f, leftEyeX + eyeRadius, eyeY)
            }
            val rightEyeArc = Path().apply {
                moveTo(rightEyeX - eyeRadius, eyeY)
                quadraticTo(rightEyeX, eyeY - eyeRadius * 1.1f, rightEyeX + eyeRadius, eyeY)
            }
            drawPath(leftEyeArc, color = Color.White, style = Stroke(width = 5f, cap = StrokeCap.Round))
            drawPath(rightEyeArc, color = Color.White, style = Stroke(width = 5f, cap = StrokeCap.Round))

            // Big open happy mouth
            val mouthY = centerY + radius * 0.28f
            val happyMouth = Path().apply {
                moveTo(centerX - 24f, mouthY)
                quadraticTo(centerX, mouthY + 22f, centerX + 24f, mouthY)
                close()
            }
            drawPath(happyMouth, color = Color(0xFFEF4444))
            drawPath(happyMouth, color = Color.White, style = Stroke(width = 3.5f))

            // Sparkles near bomb
            drawStarSparkle(centerX - radius * 0.8f, centerY - radius * 0.6f, Color(0xFFFBBF24))
            drawStarSparkle(centerX + radius * 0.85f, centerY - radius * 0.5f, Color(0xFF38BDF8))
        }
        BombMood.EXPLODED -> {
            // Handled in drawExplosionCloud
        }
    }
}

private fun DrawScope.drawSweatDrop(x: Float, y: Float) {
    val path = Path().apply {
        moveTo(x, y - 10f)
        cubicTo(x + 7f, y - 4f, x + 7f, y + 6f, x, y + 8f)
        cubicTo(x - 7f, y + 6f, x - 7f, y - 4f, x, y - 10f)
        close()
    }
    drawPath(path, color = Color(0xFF38BDF8))
    drawCircle(Color.White.copy(alpha = 0.7f), radius = 2.5f, center = Offset(x - 2f, y))
}

private fun DrawScope.drawStarSparkle(x: Float, y: Float, color: Color) {
    val path = Path().apply {
        moveTo(x, y - 12f)
        quadraticTo(x, y, x + 12f, y)
        quadraticTo(x, y, x, y + 12f)
        quadraticTo(x, y, x - 12f, y)
        quadraticTo(x, y, x, y - 12f)
        close()
    }
    drawPath(path, color = color)
}

private fun DrawScope.drawExplosionCloud(centerX: Float, centerY: Float, radius: Float) {
    // Comic cartoon puff of smoke with soot
    val puffCenters = listOf(
        Offset(centerX, centerY) to radius * 1.1f,
        Offset(centerX - radius * 0.7f, centerY - radius * 0.3f) to radius * 0.8f,
        Offset(centerX + radius * 0.7f, centerY - radius * 0.4f) to radius * 0.85f,
        Offset(centerX - radius * 0.5f, centerY + radius * 0.5f) to radius * 0.75f,
        Offset(centerX + radius * 0.5f, centerY + radius * 0.45f) to radius * 0.78f,
        Offset(centerX, centerY - radius * 0.8f) to radius * 0.7f
    )

    // Dark smoke layer
    puffCenters.forEach { (pos, r) ->
        drawCircle(Color(0xFF334155), radius = r + 8f, center = pos)
    }
    // Mid soot grey layer
    puffCenters.forEach { (pos, r) ->
        drawCircle(Color(0xFF475569), radius = r, center = pos)
    }
    // Light smoke cloud highlights
    puffCenters.forEach { (pos, r) ->
        drawCircle(Color(0xFF94A3B8), radius = r * 0.6f, center = Offset(pos.x - 6f, pos.y - 6f))
    }

    // Dizzy X eyes on the sooty bomb face
    val eyeSpacing = radius * 0.38f
    val eyeY = centerY - 10f
    drawXEye(centerX - eyeSpacing, eyeY, 14f)
    drawXEye(centerX + eyeSpacing, eyeY, 14f)

    // Funny dizzy tongue / wobbly mouth
    val dazedMouth = Path().apply {
        moveTo(centerX - 20f, centerY + 24f)
        quadraticTo(centerX - 10f, centerY + 32f, centerX, centerY + 24f)
        quadraticTo(centerX + 10f, centerY + 16f, centerX + 20f, centerY + 24f)
    }
    drawPath(dazedMouth, color = Color(0xFF0F172A), style = Stroke(width = 5f, cap = StrokeCap.Round))
}

private fun DrawScope.drawXEye(x: Float, y: Float, s: Float) {
    drawLine(Color(0xFF0F172A), Offset(x - s, y - s), Offset(x + s, y + s), strokeWidth = 5f, cap = StrokeCap.Round)
    drawLine(Color(0xFF0F172A), Offset(x + s, y - s), Offset(x - s, y + s), strokeWidth = 5f, cap = StrokeCap.Round)
}
