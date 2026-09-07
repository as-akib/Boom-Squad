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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PuzzleSymbol
import com.example.data.models.SymbolMatchingConfig
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ImmersiveCaption
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMidPurple
import com.example.ui.theme.ImmersiveSoftLilac

@Composable
fun PuzzleSymbolMatching(
    config: SymbolMatchingConfig,
    isHintActive: Boolean,
    onMatched: (isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedSymbols = remember(config) { mutableStateListOf<PuzzleSymbol>() }
    var isErrorFlash by remember(config) { mutableStateOf(false) }

    val nextRequiredSymbol = if (selectedSymbols.size < config.targetPattern.size) {
        config.targetPattern[selectedSymbols.size]
    } else null

    val infiniteTransition = rememberInfiniteTransition(label = "symbol_hint")
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
        // Clue bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF211F24), RoundedCornerShape(16.dp))
                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "GLYPH INTEL: ${config.clueText}".uppercase(),
                color = ImmersiveLavender,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }

        // Cipher legend if active
        if (config.cipherLegend.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                config.cipherLegend.entries.take(4).forEach { (code, sym) ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E1D22), RoundedCornerShape(10.dp))
                            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$code = $sym",
                            color = ImmersiveLavender,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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

        // Target pattern preview slots vs entered slots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161418), RoundedCornerShape(16.dp))
                .border(
                    1.5.dp,
                    if (isErrorFlash) DangerRed else ImmersiveCardBorder,
                    RoundedCornerShape(16.dp)
                )
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            config.targetPattern.indices.forEach { index ->
                val filledSymbol = selectedSymbols.getOrNull(index)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .padding(horizontal = 3.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (filledSymbol != null) ImmersiveMidPurple else Color(0xFF211F24)
                        )
                        .border(
                            1.dp,
                            if (filledSymbol != null) ImmersiveLavender else ImmersiveCardBorder,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filledSymbol?.glyph ?: "•",
                        fontSize = if (filledSymbol != null) 20.sp else 16.sp,
                        color = if (filledSymbol != null) Color.White else ImmersiveCaption
                    )
                }
            }

            IconButton(
                onClick = {
                    selectedSymbols.clear()
                    isErrorFlash = false
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset symbols",
                    tint = ImmersiveSoftLilac
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Available Glyph Buttons Grid
        val rows = config.availableSymbols.chunked(4)
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                row.forEach { symbol ->
                    val isTutorialDirect = config.hintClarification.startsWith("Direct clue:")
                    val isHinted = isHintActive && symbol == nextRequiredSymbol && isTutorialDirect
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .padding(4.dp)
                            .scale(if (isHinted) hintPulse else 1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isHinted) ImmersiveMidPurple else Color(0xFF36343B))
                            .border(
                                1.5.dp,
                                if (isHinted) ImmersiveLavender else ImmersiveCardBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                isErrorFlash = false
                                if (selectedSymbols.size < config.targetPattern.size) {
                                    selectedSymbols.add(symbol)
                                    // Check if complete
                                    if (selectedSymbols.size == config.targetPattern.size) {
                                        val isCorrect = selectedSymbols.zip(config.targetPattern).all { (a, b) -> a == b }
                                        if (!isCorrect) {
                                            isErrorFlash = true
                                        }
                                        onMatched(isCorrect)
                                    }
                                }
                            }
                            .testTag("symbol_button_${symbol.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = symbol.glyph,
                            fontSize = 24.sp,
                            color = if (isHinted) ImmersiveLavender else Color.White
                        )
                    }
                }
            }
        }
    }
}
