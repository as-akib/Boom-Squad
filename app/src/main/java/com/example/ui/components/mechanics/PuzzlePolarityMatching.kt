package com.example.ui.components.mechanics

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PolarityMatchingConfig
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMidPurple
import com.example.ui.theme.ImmersiveSoftLilac
import com.example.ui.theme.SuccessGreen

@Composable
fun PuzzlePolarityMatching(
    config: PolarityMatchingConfig,
    isHintActive: Boolean,
    onCompleted: (isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Current switch state map (id -> isPositive)
    val switchStates = remember(config) {
        mutableStateMapOf<Int, Boolean>().apply {
            config.switches.forEach { put(it.id, false) }
        }
    }
    var isErrorFlash by remember(config) { mutableStateOf(false) }

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
                text = "CIRCUIT INTEL: ${config.clueText}".uppercase(),
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

        // Switches Grid / Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            config.switches.forEach { switchItem ->
                val isPositive = switchStates[switchItem.id] == true

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = switchItem.label,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Toggle Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPositive) ImmersiveLavender else Color(0xFF211F24)
                            )
                            .border(
                                2.dp,
                                if (isPositive) ImmersiveSoftLilac else ImmersiveCardBorder,
                                CircleShape
                            )
                            .clickable {
                                isErrorFlash = false
                                switchStates[switchItem.id] = !isPositive
                            }
                            .testTag("polarity_switch_${switchItem.label}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isPositive) "+" else "−",
                            color = if (isPositive) ImmersiveDarkPurple else Color.Gray,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isPositive) "POSITIVE" else "NEGATIVE",
                        color = if (isPositive) ImmersiveLavender else Color.Gray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Energize Circuit Button
        Button(
            onClick = {
                val isAllCorrect = config.switches.all { switch ->
                    val currentState = switchStates[switch.id] == true
                    currentState == switch.correctPositive
                }

                if (isAllCorrect) {
                    onCompleted(true)
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
                .height(50.dp)
                .border(
                    1.dp,
                    if (isErrorFlash) DangerRed else ImmersiveLavender,
                    RoundedCornerShape(16.dp)
                )
                .testTag("energize_circuit_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = ImmersiveLavender,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "ENERGIZE POLARITY BUS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
