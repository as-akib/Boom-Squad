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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CircuitFusesConfig
import com.example.ui.theme.ArcadeBg
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.ArcadeCardBorder
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PunchyOrange

@Composable
fun PuzzleCircuitFuses(
    config: CircuitFusesConfig,
    isHintActive: Boolean,
    onCompleted: (isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val fuseStates = remember(config) {
        mutableStateMapOf<Int, Boolean>().apply {
            config.fuses.forEach { this[it.id] = false }
        }
    }
    var isErrorFlash by remember(config) { mutableStateOf(false) }

    val currentAmperage = config.fuses.filter { fuseStates[it.id] == true }.sumOf { it.amperage }
    val isBalanced = currentAmperage == config.targetAmperage

    val statusColor by animateColorAsState(
        targetValue = when {
            isErrorFlash -> DangerRed
            isBalanced -> NeonLime
            currentAmperage > config.targetAmperage -> DangerRed
            else -> ElectricCyan
        },
        animationSpec = tween(durationMillis = 200),
        label = "fuse_status_color"
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = BrightYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "BREAKER MATRIX CLUE",
                        color = BrightYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
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

        Spacer(modifier = Modifier.height(14.dp))

        // Power Load Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ArcadeBg, RoundedCornerShape(12.dp))
                .border(1.5.dp, statusColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CIRCUIT LOAD",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${currentAmperage}A / ${config.targetAmperage}A",
                    color = statusColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.2f), CircleShape)
                    .border(1.dp, statusColor, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = when {
                        isBalanced -> "STABILIZED"
                        currentAmperage > config.targetAmperage -> "OVERLOAD!"
                        else -> "UNDERLOAD"
                    },
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Fuse Bank Grid
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            config.fuses.forEach { fuse ->
                val isOn = fuseStates[fuse.id] == true
                val fuseColor = Color(fuse.colorHex)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isOn) fuseColor.copy(alpha = 0.25f) else ArcadeBg)
                        .border(
                            width = if (isOn) 2.dp else 1.dp,
                            color = if (isOn) fuseColor else ArcadeCardBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            fuseStates[fuse.id] = !isOn
                            isErrorFlash = false
                        }
                        .padding(vertical = 12.dp, horizontal = 4.dp)
                        .testTag("fuse_toggle_${fuse.id}")
                ) {
                    // Indicator LED
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(if (isOn) fuseColor else Color.DarkGray, CircleShape)
                            .border(1.dp, if (isOn) Color.White else Color.Gray, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${fuse.amperage}A",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = fuse.label,
                        color = if (isOn) fuseColor else Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Breaker Trigger Button
        Button(
            onClick = {
                if (isBalanced) {
                    onCompleted(true)
                } else {
                    isErrorFlash = true
                    onCompleted(false)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isBalanced) NeonLime else PunchyOrange,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("circuit_fuses_submit_button")
        ) {
            Text(
                text = "ENGAGE CIRCUIT BREAKER",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}
