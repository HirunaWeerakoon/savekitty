package com.example.savekitty.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.savekitty.R
import androidx.compose.ui.tooling.preview.Preview
import com.example.savekitty.ui.theme.SaveKittyTheme
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun RoomScreen(
    currentHealth: Int = 5,
    onTableClick: () -> Unit,
    onDoorClick: () -> Unit,
    onBowlClick: () -> Unit,
    onCatClick: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
            .systemBarsPadding()
    ) {
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
        val doorSource = remember { MutableInteractionSource() }
        val isDoorPressed by doorSource.collectIsPressedAsState()

        // 🚪 DOOR
        Image(
            painter = painterResource(id = R.drawable.door),
            contentDescription = "Shop",
            contentScale = ContentScale.FillBounds,
            colorFilter = getPressColorFilter(isDoorPressed),
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.565f,
                    y = screenHeight * 0.225f
                )
                .size(width = screenWidth * 0.255f, height = screenHeight * 0.39f) // Width of the door
                .gameClick(interactionSource = doorSource) { onDoorClick() }
        )

        // 🪑 TABLE
        val tableSource = remember { MutableInteractionSource() }
        val isTablePressed by tableSource.collectIsPressedAsState()
        Image(
            painter = painterResource(id = R.drawable.table),
            contentDescription = "Work Table",
            contentScale = ContentScale.FillBounds,
            colorFilter = getPressColorFilter(isTablePressed),
            modifier = Modifier
                .offset(
                    x = screenWidth * 0f,
                    y = screenHeight * 0.57f
                )
                .size(width = screenWidth * 0.45f, height = screenHeight * 0.43f) // Width of the table
                .gameClick (interactionSource = tableSource) { onTableClick() }

        )

        // 🥣 BOWL
        val bowlSource = remember { MutableInteractionSource() }
        val isBowlPressed by bowlSource.collectIsPressedAsState()
        Image(
            painter = painterResource(id = R.drawable.bowl),
            contentDescription = "Food Bowl",
            contentScale = ContentScale.FillBounds,
            colorFilter = getPressColorFilter(isBowlPressed),
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.515f,
                    y = screenHeight * 0.76f
                )
                .size(width = screenWidth * 0.3f, height = screenHeight * 0.11f) // Width of the bowl
                .gameClick (interactionSource = bowlSource) { onBowlClick() }
        )


        // --- LAYER 3: THE KITTY 🐱 ---

        val isHappy = currentHealth > 2
        val catImage = if (isHappy) R.drawable.cat_sleep else R.drawable.cat_hungry

        // 1. CONFIGURE SIZES SEPARATELY 📏
        // -------------------------------------------------
        // 💤 SLEEPING CAT SIZE (On the table)
        val sleepWidth  = screenWidth * 0.25f
        val sleepHeight = screenWidth * 0.25f

        // 😿 HUNGRY CAT SIZE (By the bowl)
        // Change these numbers to resize ONLY the hungry cat!
        val hungryWidth  = screenWidth * 0.445f
        val hungryHeight = screenWidth * 0.445f
        // -------------------------------------------------

        // Logic to pick the right size
        val currentWidth = if (isHappy) sleepWidth else hungryWidth
        val currentHeight = if (isHappy) sleepHeight else hungryHeight

        // 2. CONFIGURE POSITIONS SEPARATELY 📍
        val catX = if (isHappy) screenWidth * 0.715f else screenWidth * 0.30f
        val catY = if (isHappy) screenHeight * 0.54f else screenHeight * 0.6f

        val catSource = remember { MutableInteractionSource() }

        Image(
            painter = painterResource(id = catImage),
            contentDescription = "Kitty",
            // Use Fit so it doesn't get distorted
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = catX, y = catY)
                // Use the dynamic variables we created above
                .size(width = currentWidth, height = currentHeight)
                .gameClick(interactionSource = catSource) { onCatClick() }
        )
    }
}



// A custom modifier that makes any image "Squish" and "Brighten" when clicked
fun Modifier.gameClick(
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()

    // 1. ANIMATION: Scale down to 0.95 when pressed, back to 1.0 when released
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scale")

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null, // Disable the standard Android "Ripple" wave (looks bad on pixel art)
            onClick = onClick
        )
}

// Helper to make the image brighter when pressed
@Composable
fun getPressColorFilter(isPressed: Boolean = false): ColorFilter? {
    if (!isPressed) return null
    // Create a matrix that brightens the image (simulates a light turning on)
    val matrix = ColorMatrix(floatArrayOf(
        1f, 0f, 0f, 0f, 50f, // Red + 50 brightness
        0f, 1f, 0f, 0f, 50f, // Green + 50 brightness
        0f, 0f, 1f, 0f, 50f, // Blue + 50 brightness
        0f, 0f, 0f, 1f, 0f   // Alpha unchanged
    ))
    return ColorFilter.colorMatrix(matrix)
}

@Preview(showBackground = true)
@Composable
fun RoomScreenPreview() {
    SaveKittyTheme {
        RoomScreen(
            currentHealth = 1,
            onTableClick = {},
            onDoorClick = {},
            onBowlClick = {}
        )
    }
}
