package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveLavender

@Composable
fun TimerHud(
    secondsRemaining: Int,
    modifier: Modifier = Modifier
) {
    val isUrgent = secondsRemaining <= 10

    val infiniteTransition = rememberInfiniteTransition(label = "timer_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isUrgent) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val textColor by animateColorAsState(
        targetValue = if (isUrgent) DangerRed else ImmersiveLavender,
        label = "timer_color"
    )

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .testTag("timer_hud")
            .scale(pulseScale)
            .background(ImmersiveCard, RoundedCornerShape(16.dp))
            .border(1.dp, if (isUrgent) DangerRed else ImmersiveCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Timer Icon",
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = timeFormatted,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }
    }
}
