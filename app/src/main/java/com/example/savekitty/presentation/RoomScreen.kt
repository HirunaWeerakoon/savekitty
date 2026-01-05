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
import com.example.savekitty.presentation.FeedingPopup
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savekitty.data.Food
import com.example.savekitty.presentation.GameOverlay
import com.example.savekitty.presentation.MuteButton
import com.example.savekitty.presentation.SpriteAnimation.SpriteAnimation

@Composable
fun RoomScreen(
    currentHealth: Int = 10,
    coinCount: Int = 100,
    onTableClick: () -> Unit,
    fishCount: Int,
    onBuyFish: () -> Unit,
    onFeedCat: () -> Unit,
    onDoorClick: () -> Unit,
    onCatClick: () -> Unit = {},
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    inventory: Map<String, Int>,
    onEat: (Food) -> Unit

) {
    var showFeedingPopup by remember { mutableStateOf(false) }
    val fireFrames = listOf(
        R.drawable.prop_fireplace_0,
        R.drawable.prop_fireplace_1,
        R.drawable.prop_fireplace_2,
        R.drawable.prop_fireplace_3,
        R.drawable.prop_fireplace_2,
        R.drawable.prop_fireplace_1,
        R.drawable.prop_fireplace_0,
    )

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
        SpriteAnimation(
            frames = fireFrames,
            frameDurationMillis = 150, // Fast flicker
            modifier = Modifier
                .align(Alignment.BottomCenter) // Rough position
                .offset(x = (-113).dp, y = (-280).dp) // <--- TWEAK THIS X/Y TO FIT YOUR ART
                .size(140.dp) // <--- TWEAK SIZE
        )

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
                .gameClick(interactionSource = bowlSource) {
                    showFeedingPopup = true
                }
        )


        // --- LAYER 3: THE KITTY 🐱 ---

        val isHappy = currentHealth > 4
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
        GameOverlay(
            health = currentHealth,
            coinCount = coinCount,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        MuteButton(
            isMuted = isMuted,
            onToggle = onToggleMute,
            modifier = Modifier.align(Alignment.TopStart)
        )
        // --- LAYER 5: POPUPS ---
        if (showFeedingPopup) {
            // 2. UPDATED POPUP CALL
            FeedingPopup(
                inventory = inventory, // Pass the Map
                onEat = { food -> onEat(food) }, // Pass the action
                onClose = { showFeedingPopup = false }
            )
        }
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
// In RoomScreen.kt

@Preview(showBackground = true)
@Composable
fun RoomScreenPreview() {
    SaveKittyTheme {
        RoomScreen(
            currentHealth = 3,
            onTableClick = {},
            onDoorClick = {},
            onCatClick = {},
            coinCount = 100,
            fishCount = 0,
            onBuyFish = {},
            onFeedCat = {},
            isMuted = false,
            onToggleMute = {},
            inventory = emptyMap(),
            onEat = {}

        )
    }
}

