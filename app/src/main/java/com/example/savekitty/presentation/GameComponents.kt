package com.example.savekitty.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savekitty.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// 1. THE HUD (Hearts & Biscuits)
@Composable
fun GameOverlay(
    health: Int,
    coinCount: Int,
    modifier: Modifier = Modifier,
    onWatchAd: () -> Unit = {} // New callback for the ad button
) {
    // State to track which popup is visible
    var showHealthInfo by remember { mutableStateOf(false) }
    var showBiscuitInfo by remember { mutableStateOf(false) }

    Box(modifier = modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- HEALTH CLICKABLE ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { showHealthInfo = true } // Open Health Info
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                HealthBar(health = health)
            }

            // --- BISCUIT CLICKABLE ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { showBiscuitInfo = true } // Open Biscuit Info
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = coinCount.toString(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_biscuit),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    // --- POPUPS ---
    if (showHealthInfo) {
        InfoPopup(
            title = "Kitty's Health",
            description = "This represents how happy and well-fed your kitty is. If it reaches 0, Kitty will be admitted to the hospital! Keep it high by feeding him fish.",
            iconRes = R.drawable.ic_heart_full,
            onDismiss = { showHealthInfo = false }
        )
    }

    if (showBiscuitInfo) {
        InfoPopup(
            title = "Cat Biscuits",
            description = "The currency of the cat world. Earn biscuits by completing focus sessions or by clicking on your cat when he's hungry. Use them to buy food and decorations!",
            iconRes = R.drawable.ic_biscuit,
            onDismiss = { showBiscuitInfo = false },
            isAdButtonVisible = true,
            onWatchAdClick = {
                showBiscuitInfo = false
                onWatchAd()
            }
        )
    }
}

// 2. THE MUTE BUTTON 🔇
@Composable
fun MuteButton(
    isMuted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
            .padding(16.dp)
            .systemBarsPadding()
            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            .size(48.dp)
    ) {
        // Use standard icons if you don't have custom pngs yet
        val iconRes = if (isMuted) R.drawable.ic_volume_off else R.drawable.ic_volume_up
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "Mute Toggle",
            contentScale = ContentScale.Fit,
            modifier = Modifier.padding(10.dp) // Adjust padding to make icon fit nicely inside circle
        )
    }
}
@Composable
fun InfoPopup(
    title: String,
    description: String,
    iconRes: Int,
    onDismiss: () -> Unit,
    isAdButtonVisible: Boolean = false,
    onWatchAdClick: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8E9)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF2E3333), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E3333)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = description,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (isAdButtonVisible) {
                    Button(
                        onClick = onWatchAdClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008080)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Watch Ad (+5 🍪)")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                TextButton(onClick = onDismiss) {
                    Text("Close", color = Color.Gray)
                }
            }
        }
    }
}
@Composable
fun HealthBar(health: Int, maxHealth: Int = 10) {
    Row(modifier = Modifier.padding(8.dp)) {
        // We display 5 hearts. Each heart represents 2 health points.
        repeat(5) { index ->
            val heartValue = (index + 1) * 2
            val resource = when {
                health >= heartValue -> R.drawable.ic_heart_full
                health >= heartValue - 1 -> R.drawable.ic_heart_half
                else -> R.drawable.ic_heart_empty
            }

            Image(
                painter = painterResource(id = resource),
                contentDescription = "Heart",
                modifier = Modifier.size(24.dp).padding(end = 4.dp)
            )
        }
    }
}