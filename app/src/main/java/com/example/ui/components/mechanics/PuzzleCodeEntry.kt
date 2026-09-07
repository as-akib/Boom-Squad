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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.models.CodeEntryConfig
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMuted
import com.example.ui.theme.ImmersiveSoftLilac
import com.example.ui.theme.SuccessGreen

@Composable
fun PuzzleCodeEntry(
    config: CodeEntryConfig,
    isHintActive: Boolean,
    onCodeEntered: (isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredText by remember(config) { mutableStateOf("") }
    var isErrorFlash by remember(config) { mutableStateOf(false) }

    val maxLen = config.length

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
                text = "SECURITY INTEL: ${config.clueText}".uppercase(),
                color = ImmersiveLavender,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }

        // Scattered bomb casing markings if present
        if (config.scatteredClues.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                config.scatteredClues.forEach { clueItem ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E1D22), RoundedCornerShape(10.dp))
                            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = clueItem,
                            color = ImmersiveLavender,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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

        Spacer(modifier = Modifier.height(12.dp))

        // Digital LCD Display
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(48.dp)
                .background(Color(0xFF161418), RoundedCornerShape(16.dp))
                .border(
                    1.5.dp,
                    if (isErrorFlash) DangerRed else ImmersiveLavender,
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            val displayDigits = enteredText.padEnd(maxLen, '_')
            Text(
                text = displayDigits.toCharArray().joinToString(" "),
                color = if (isErrorFlash) DangerRed else ImmersiveLavender,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Keypad in Immersive UI pill buttons
        val keypad = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("CLR", "0", "OK")
        )

        keypad.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                row.forEach { key ->
                    KeypadButton(
                        label = key,
                        onClick = {
                            isErrorFlash = false
                            when (key) {
                                "CLR" -> enteredText = ""
                                "OK" -> {
                                    if (enteredText.length == maxLen) {
                                        val correct = enteredText == config.targetCode
                                        if (!correct) {
                                            isErrorFlash = true
                                        }
                                        onCodeEntered(correct)
                                    }
                                }
                                else -> {
                                    if (enteredText.length < maxLen) {
                                        enteredText += key
                                        if (enteredText.length == maxLen) {
                                            val correct = enteredText == config.targetCode
                                            if (!correct) {
                                                isErrorFlash = true
                                            }
                                            onCodeEntered(correct)
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    label: String,
    onClick: () -> Unit
) {
    val isOk = label == "OK"
    val isClr = label == "CLR"

    val bgColor = when {
        isOk -> ImmersiveLavender
        isClr -> DangerRed.copy(alpha = 0.25f)
        else -> Color(0xFF36343B)
    }

    val contentColor = when {
        isOk -> ImmersiveDarkPurple
        isClr -> DangerRed
        else -> ImmersiveSoftLilac
    }

    Box(
        modifier = Modifier
            .size(width = 68.dp, height = 44.dp)
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("keypad_$label"),
        contentAlignment = Alignment.Center
    ) {
        if (isOk) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Submit", tint = contentColor, modifier = Modifier.size(20.dp))
        } else if (isClr) {
            Icon(imageVector = Icons.Default.Backspace, contentDescription = "Clear", tint = contentColor, modifier = Modifier.size(18.dp))
        } else {
            Text(
                text = label,
                color = contentColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
