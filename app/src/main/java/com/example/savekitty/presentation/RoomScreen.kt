package com.example.savekitty.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

        // --- LAYER 1: BACKGROUND ---
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop, // Fills the screen
            modifier = Modifier.fillMaxSize()
        )

        // --- LAYER 2: FURNITURE (Clickable) ---

        // 🚪 DOOR
        Image(
            painter = painterResource(id = R.drawable.door),
            contentDescription = "Shop",
            modifier = Modifier
                // ADJUST THESE NUMBERS TO MOVE THE DOOR!
                .offset(
                    x = screenWidth * 0.6f, // 60% from left
                    y = screenHeight * 0.2f  // 20% from top
                )
                .size(screenWidth * 0.25f) // Width is 25% of screen
                .clickable { onDoorClick() }
        )

        // 🪑 TABLE (Productivity Zone)
        Image(
            painter = painterResource(id = R.drawable.table),
            contentDescription = "Work Table",
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.05f, // 5% from left
                    y = screenHeight * 0.5f  // 50% from top
                )
                .size(screenWidth * 0.5f) // Big table
                .clickable { onTableClick() }
        )

        // 🥣 BOWL (Feeding Zone)
        Image(
            painter = painterResource(id = R.drawable.bowl),
            contentDescription = "Food Bowl",
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.4f, // 40% from left
                    y = screenHeight * 0.7f  // 70% from top
                )
                .size(screenWidth * 0.15f)
                .clickable { onBowlClick() }
        )

        // --- LAYER 3: THE KITTY 🐱 ---
        // Logic: If health is full, sleep on table. Else, sit by bowl.
        val isHealthy = currentHealth == 5

        // Example Kitty Position (We can refine this once you see the furniture)
        val kittyX = if (isHealthy) screenWidth * 0.15f else screenWidth * 0.45f
        val kittyY = if (isHealthy) screenHeight * 0.45f else screenHeight * 0.65f

        // Placeholder for Kitty (Uses a generic icon until you have the sprite)
        // You can replace Icons.Default.Face with your R.drawable.cat_happy
        /* Image(
             painter = painterResource(id = if (isHealthy) R.drawable.cat_sleep else R.drawable.cat_hungry),
             contentDescription = "Kitty",
             modifier = Modifier
                 .offset(x = kittyX, y = kittyY)
                 .size(100.dp)
        )
        */
    }
}