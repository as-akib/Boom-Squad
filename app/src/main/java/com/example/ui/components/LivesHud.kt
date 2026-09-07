package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveMidPurple
import com.example.ui.theme.ImmersiveSoftLilac

@Composable
fun LivesHud(
    lives: Int,
    remainingRefillMs: Long = 0L,
    maxLives: Int = 5,
    onAddLivesClick: () -> Unit = {},
    onOpenStoreClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val refillMin = (remainingRefillMs / 1000L / 60L).toInt()
    val refillSec = ((remainingRefillMs / 1000L) % 60L).toInt()
    val timerStr = if (lives < maxLives && remainingRefillMs > 0L) {
        String.format("%02d:%02d", refillMin, refillSec)
    } else null

    Box(
        modifier = modifier
            .testTag("lives_hud")
            .background(ImmersiveCard, RoundedCornerShape(16.dp))
            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (i in 1..maxLives) {
                        val isAlive = i <= lives
                        Icon(
                            imageVector = if (isAlive) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isAlive) "Active Life $i" else "Lost Life $i",
                            tint = if (isAlive) DangerRed else ImmersiveCardBorder,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 2.dp)
                        )
                    }
                }
                if (timerStr != null) {
                    Text(
                        text = "+1 in $timerStr",
                        color = ImmersiveSoftLilac,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }

            if (lives < maxLives) {
                Spacer(modifier = Modifier.width(8.dp))
                // Refill with Ads button
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(ImmersiveMidPurple)
                        .clickable { onAddLivesClick() }
                        .testTag("refill_lives_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.OndemandVideo,
                        contentDescription = "Refill Energy with Ad",
                        tint = ImmersiveSoftLilac,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Refill with money from Store button (if provided)
                if (onOpenStoreClick != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF211F24))
                            .border(1.dp, ImmersiveCardBorder, CircleShape)
                            .clickable { onOpenStoreClick() }
                            .testTag("refill_shop_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Buy Energy from Shop",
                            tint = ImmersiveSoftLilac,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
