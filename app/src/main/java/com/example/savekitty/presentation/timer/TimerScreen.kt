package com.example.savekitty.presentation.timer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savekitty.R
import com.example.savekitty.ui.RoomScreen
import com.example.savekitty.ui.theme.SaveKittyTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import com.example.savekitty.data.TodoItem
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.graphics.ImageBitmap
import com.example.savekitty.presentation.timer.NotebookDialog
import com.example.savekitty.presentation.pixelClickable
import com.example.savekitty.presentation.GameOverlay
import com.example.savekitty.presentation.MuteButton
import com.example.savekitty.presentation.SpriteAnimation.SpriteAnimation
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun TimerScreen(
    timeLeft: Int,
    isTimerRunning: Boolean,
    todoList: List<TodoItem>, // <--- NEW
    onAddTodo: (String, Boolean) -> Unit, // <--- NEW
    onToggleTodo: (Long) -> Unit, // <--- NEW
    onDeleteTodo: (Long) -> Unit, // <--- NEW
    currentHealth: Int, // <--- Add this
    coinCount: Int,     // <--- Add this
    onToggleMute: () -> Unit, // <--- Add this
    onLaptopClick: () -> Unit,
    onBackClick: () -> Unit,
    onBooksClick: () -> Unit,
    isMuted: Boolean,
    onPlayPageTurn: () -> Unit,
    onWatchAd: () -> Unit

) {
    var showNotebook by remember { mutableStateOf(false) }

    val rainFrames = listOf(
        R.drawable.rain_frame_1,
        R.drawable.rain_frame_2,
        R.drawable.rain_frame_3,
        R.drawable.rain_frame_4,
        R.drawable.rain_frame_5,
        R.drawable.rain_frame_4,
        R.drawable.rain_frame_3,
        R.drawable.rain_frame_2,

    )
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()

    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val isPreview = LocalInspectionMode.current
        val notebookBitmap = if (isPreview) {
            ImageBitmap(1, 1)
        } else {
            ImageBitmap.imageResource(id = R.drawable.prop_notebook)
        }

        val booksBitmap = if (isPreview) {
            ImageBitmap(1, 1)
        } else {
            ImageBitmap.imageResource(id = R.drawable.prop_books)
        }

        SpriteAnimation(
            frames = rainFrames,
            frameDurationMillis = 150,
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.1f, // <--- TWEAK THIS
                    y = screenHeight * (-0.05f)  // <--- TWEAK THIS
                )
                .size(screenWidth * 0.85f) // Size relative to width
        )
        // --- LAYER 1: BACKGROUND (The Desk) ---
        Image(
            painter = painterResource(id = R.drawable.bg_desk), // Make sure you have this!
            contentDescription = "Desk",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // --- LAYER 2: THE LAMP 💡 ---
        var isLampOn by remember { mutableStateOf(false) }
        Image(
            painter = painterResource(id = if (isLampOn) R.drawable.prop_lamp_on else R.drawable.prop_lamp_off),
            contentDescription = "Lamp",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = screenWidth * (-0.104f), y = screenHeight * 0.35f) // Adjust these!
                .size(screenWidth * 0.5f)
                .clickable {
                    isLampOn = !isLampOn

                }
        )

        // --- LAYER 3: BOOKS & CAT 📚🐱 ---
        // The Book Stack
        Image(
            bitmap = booksBitmap,
            contentDescription = "Stats",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = screenWidth * 0.24f, y = screenHeight * 0.63f)
                .size(screenWidth * 0.33f)
                .pixelClickable(imageBitmap = booksBitmap) { onBooksClick() }
        )

        // The Cat (Sitting on books)
        Image(
            painter = painterResource(id = R.drawable.prop_cat),
            contentDescription = "Cat",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = screenWidth * 0.253f, y = screenHeight * 0.281f) // Slightly above books
                .size(screenWidth * 0.4f)
                .clickable {
                    // TODO: Play Meow Sound
                }
        )

        // --- LAYER 4: THE NOTEBOOK (Todo) 📝 ---
        Image(
            bitmap = notebookBitmap,
            contentDescription = "Notebook",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = screenWidth * 0.22f, y = screenHeight * 0.55f)
                .size(screenWidth * 0.4f)
                .pixelClickable(imageBitmap = notebookBitmap) {
                    onPlayPageTurn()
                    showNotebook = true
                }

        )

        // --- LAYER 5: THE LAPTOP 💻 ---
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = screenHeight * 0f,x=screenWidth*0.2f) // Move down a bit
                .size(screenWidth * 0.58f, screenWidth * 0.55f) // Adjust aspect ratio
                .clickable { onLaptopClick() } // <--- NAVIGATE TO ZOOM
        ) {
            // A. The Laptop Body (Frame)
            Image(
                painter = painterResource(id = R.drawable.prop_laptop_body),
                contentDescription = "Laptop",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // B. The "Live" Screen Content (Drawn BEHIND the frame logically, or on top if frame is transparent)
            // We draw the time small here so you can see it from the desk view
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = 35.dp)
                    .rotate(7f)
                    .offset(y = (-27).dp),

                contentAlignment = Alignment.Center
            ) {
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    color = Color(0xFF000000), // Hacker Green
                    fontFamily = FontFamily.Monospace, // Pixel Font
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        // --- LAYER 6: UI CONTROLS (Back Button) 🔙 ---
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .size(48.dp)

        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "Exit",
                modifier = Modifier
            )
        }
        // --- LAYER 7: NOTEBOOK OVERLAY ---
        if (showNotebook) {
            NotebookDialog(
                tasks = todoList,
                onAdd = onAddTodo,
                onToggle = onToggleTodo,
                onDelete = onDeleteTodo,
                onClose = { showNotebook = false }
            )
        }
        // --- LAYER 8: HUD & MUTE (On Top) ---
        // 1. The Stats (Top Right)
        GameOverlay(
            health = currentHealth,
            coinCount = coinCount,
            modifier = Modifier.align(Alignment.TopCenter),
            onWatchAd = { onWatchAd() }
        )

        // 2. The Mute Button (Top Left)
        MuteButton(
            isMuted = isMuted,
            onToggle = onToggleMute,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

@Preview(showBackground = true,widthDp = 1080, heightDp = 2400)
@Composable
fun TimerScreenPreview() {
    SaveKittyTheme {
        TimerScreen(
            timeLeft = 20,
            isTimerRunning = true,
            onLaptopClick = {},
            onBackClick = {},
            todoList = emptyList(),
            onToggleTodo = {},
            onDeleteTodo = {},
            onBooksClick = {},
            currentHealth = 3,
            coinCount = 100,
            onToggleMute = {},
            isMuted = false,
            onPlayPageTurn = {},
            onAddTodo = { _, _ -> },
            onWatchAd = {}

        )
    }

}

