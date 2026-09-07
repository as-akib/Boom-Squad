package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ArcadeBg
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.ArcadeCardBorder
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PunchyOrange

/**
 * True modal Mission Briefing Dialog.
 * User CANNOT click any background elements or dismiss by tapping outside.
 * ONLY "PROCEED WITH DEFUSAL" closes the modal and begins gameplay.
 */
@Composable
fun TutorialOverlay(
    missionTitle: String = "Ordnance Defusal",
    stepInstruction: String,
    timeLimitSeconds: Int = 60,
    mechanicsCount: Int = 1,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "briefing_bounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Dialog(
        onDismissRequest = {
            // Strictly disallow dismissing by clicking outside or back press
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .testTag("mission_briefing_modal"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                ArcadeCard,
                                ArcadeBg
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(ElectricCyan, PunchyOrange, BrightYellow)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = ElectricCyan.copy(alpha = 0.5f)
                    )
                    .padding(24.dp)
            ) {
                // Header Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(PunchyOrange.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .border(1.dp, PunchyOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = BrightYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MISSION BRIEFING",
                        color = BrightYellow,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mission Title
                Text(
                    text = missionTitle.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Metrics Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(ArcadeCardBorder.copy(alpha = 0.5f), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${timeLimitSeconds}s TIME",
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(NeonLime.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, NeonLime.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$mechanicsCount MODULE${if (mechanicsCount > 1) "S" else ""}",
                            color = NeonLime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Objective Text Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ArcadeBg.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                        .border(1.dp, ArcadeCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = stepInstruction,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Animated bouncing indicator
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier
                        .size(24.dp)
                        .offset(y = bounceOffset.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Single actionable button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PunchyOrange,
                        contentColor = Color.White
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = CircleShape,
                            spotColor = PunchyOrange.copy(alpha = 0.6f)
                        )
                        .testTag("tutorial_got_it_button")
                ) {
                    Text(
                        text = "PROCEED WITH DEFUSAL",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}
