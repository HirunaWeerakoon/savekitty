package com.example.savekitty.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Import this for the debug color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.savekitty.R

@Composable
fun RoomScreen(
    currentHealth: Int = 5,
    onTableClick: () -> Unit,
    onDoorClick: () -> Unit,
    onBowlClick: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // --- LAYER 1: BACKGROUND (The Artwork) ---
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --- LAYER 2: INVISIBLE HOTSPOTS (Clickable Zones) ---
        // Adjust the numbers (0.5f = 50%) to match your specific image!

        // 🚪 DOOR HOTSPOT
        Box(
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.6f, // Move Left/Right
                    y = screenHeight * 0.2f  // Move Up/Down
                )
                .size(screenWidth * 0.25f) // How big is the door?
                // 👇 TEMPORARY: Keep this RED line while you adjust sizes. Delete it when done!
                .background(Color.Red.copy(alpha = 0.3f))
                .clickable { onDoorClick() }
        )

        // 🪑 TABLE HOTSPOT
        Box(
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.05f,
                    y = screenHeight * 0.5f
                )
                .size(screenWidth * 0.5f)
                .background(Color.Blue.copy(alpha = 0.3f)) // Debug Color
                .clickable { onTableClick() }
        )

        // 🥣 BOWL HOTSPOT
        Box(
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.4f,
                    y = screenHeight * 0.7f
                )
                .size(screenWidth * 0.15f)
                .background(Color.Green.copy(alpha = 0.3f)) // Debug Color
                .clickable { onBowlClick() }
        )

        // --- LAYER 3: THE KITTY ---
        // (We can add the kitty back once the furniture works!)
    }
}