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
import com.example.savekitty.ui.FeedingPopup
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
    onCatClick: () -> Unit = {}
) {
    var showFeedingPopup by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
            .systemBarsPadding()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val biscuitFrames = listOf(
            R.drawable.cat_knead_0,
            R.drawable.cat_knead_1,
            R.drawable.cat_knead_2,
            R.drawable.cat_knead_1
        )

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
            coinCount = coinCount
        )
        // --- LAYER 5: POPUPS ---
        if (showFeedingPopup) {
            FeedingPopup(
                fishCount = fishCount,
                onFeed = {
                    onFeedCat()
                    // Optional: Close popup after feeding?
                    // showFeedingPopup = false
                },
                onClose = { showFeedingPopup = false },
                onBuyFish = onBuyFish
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

@Composable
fun GameOverlay(
    health: Int,      // Scale: 0 to 10 (10 = 5 Hearts, 9 = 4.5 Hearts)
    coinCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(), // Keeps it safe from status bars
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // --- HEART SECTION ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // We draw 5 hearts total
            for (i in 1..5) {
                // Math to decide which image to show:
                // Full Heart Value: i * 2 (e.g., Heart 1 needs 2 health to be full)
                // Half Heart Value: (i * 2) - 1

                val imageRes = when {
                    health >= i * 2 -> R.drawable.ic_heart_full      // Case: Completely Full
                    health == (i * 2) - 1 -> R.drawable.ic_heart_half // Case: Half Full
                    else -> R.drawable.ic_heart_empty                 // Case: Empty
                }

                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // --- COIN SECTION ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$coinCount",
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.ic_coin),
                contentDescription = "Coins",
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
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
            onFeedCat = {}

        )
    }
}

