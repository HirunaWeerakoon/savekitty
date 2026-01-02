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

// 1. THE HUD (Hearts & Biscuits)
@Composable
fun GameOverlay(
    health: Int,
    coinCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .systemBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // --- HEART SECTION ---
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 1..5) {
                val imageRes = when {
                    health >= i * 2 -> R.drawable.ic_heart_full
                    health == (i * 2) - 1 -> R.drawable.ic_heart_half
                    else -> R.drawable.ic_heart_empty
                }
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // --- BISCUIT SECTION ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$coinCount",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
            Image(
                painter = painterResource(id = R.drawable.ic_biscuit), // Make sure you have this icon!
                contentDescription = "Biscuits",
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
        }
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
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "Mute Toggle",
            tint = Color.White,
            modifier = Modifier.padding(12.dp)
        )
    }
}