package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ImmersiveCaption
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMidPurple
import com.example.ui.theme.ImmersiveMuted
import com.example.ui.theme.ImmersiveSoftLilac

/**
 * Placeholder IAP store interface.
 *
 * TODO: BEFORE PRODUCTION RELEASE:
 * 1. Integrate the Google Play Billing Library:
 *    `implementation("com.android.billingclient:billing-ktx:7.0.0")`
 * 2. Connect `BillingClient.newBuilder(context)` to query SKU product details and launch billing flow.
 * 3. Grant the purchased item (e.g., unlimited lives or instant refill) upon `PurchasesUpdatedListener` callback.
 */
@Composable
fun StoreDialog(
    onDismiss: () -> Unit,
    onSimulatePurchase: (itemName: String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(32.dp))
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = ImmersiveLavender.copy(alpha = 0.3f)
                )
                .testTag("store_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TACTICAL ARMORY",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-0.5).sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = ImmersiveSoftLilac)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                StoreItemCard(
                    icon = Icons.Default.Bolt,
                    iconTint = ImmersiveLavender,
                    title = "Instant Energy Refill",
                    description = "Instantly restore all 5 energy units to max",
                    price = "$0.99",
                    onClick = { onSimulatePurchase("5-Energy Refill") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                StoreItemCard(
                    icon = Icons.Default.Diamond,
                    iconTint = ImmersiveLavender,
                    title = "Infinite Energy Protocol",
                    description = "Operate without countdowns or energy limits",
                    price = "$2.99",
                    onClick = { onSimulatePurchase("Infinite Energy Pass") }
                )
            }
        }
    }
}

@Composable
private fun StoreItemCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    price: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF211F24), RoundedCornerShape(20.dp))
            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(20.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(ImmersiveMidPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(description, color = ImmersiveMuted, fontSize = 10.sp, lineHeight = 14.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = ImmersiveLavender,
                contentColor = ImmersiveDarkPurple
            ),
            shape = CircleShape,
            modifier = Modifier.height(34.dp)
        ) {
            Text(price, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
    }
}
