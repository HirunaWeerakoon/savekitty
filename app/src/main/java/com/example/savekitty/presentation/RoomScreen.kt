package com.example.savekitty.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.savekitty.R
import androidx.compose.ui.tooling.preview.Preview
import com.example.savekitty.ui.theme.SaveKittyTheme

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

        // --- LAYER 1: THE EMPTY ROOM ---
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds, // Stretches to fit any phone perfectly
            modifier = Modifier.fillMaxSize()
        )

        // --- LAYER 2: THE OBJECTS (Real Images) ---

        // 🚪 DOOR
        Image(
            painter = painterResource(id = R.drawable.door),
            contentDescription = "Shop",
            contentScale = ContentScale.FillBounds, // Keeps the door's aspect ratio
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.565f,
                    y = screenHeight * 0.225f
                )
                .size(width = screenWidth * 0.255f, height = screenHeight * 0.39f) // Width of the door
                .clickable { onDoorClick() }
        )

        // 🪑 TABLE
        Image(
            painter = painterResource(id = R.drawable.table),
            contentDescription = "Work Table",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(
                    x = screenWidth * 0f,
                    y = screenHeight * 0.57f
                )
                .size(width = screenWidth * 0.45f, height = screenHeight * 0.43f) // Width of the table
                .clickable { onTableClick() }
        )

        // 🥣 BOWL
        Image(
            painter = painterResource(id = R.drawable.bowl),
            contentDescription = "Food Bowl",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.515f,
                    y = screenHeight * 0.76f
                )
                .size(width = screenWidth * 0.3f, height = screenHeight * 0.11f) // Width of the bowl
                .clickable { onBowlClick() }
        )

        // --- LAYER 3: THE KITTY (Coming soon) ---
    }
}

@Preview(showBackground = true)
@Composable
fun RoomScreenPreview() {
    SaveKittyTheme {
        RoomScreen(
            currentHealth = 5,
            onTableClick = {},
            onDoorClick = {},
            onBowlClick = {}
        )
    }
}