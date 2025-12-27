package com.example.savekitty.presentation.timer

import androidx.compose.foundation.Image
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
import com.example.savekitty.viewModel.TodoItem

@Composable
fun TimerScreen(
    timeLeft: Int,
    isTimerRunning: Boolean,
    todoList: List<TodoItem>, // <--- NEW
    onAddTodo: (String) -> Unit, // <--- NEW
    onToggleTodo: (Long) -> Unit, // <--- NEW
    onDeleteTodo: (Long) -> Unit, // <--- NEW
    onLaptopClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var showNotebook by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
            .systemBarsPadding()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

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
                .offset(x = screenWidth * (-0.104f), y = screenHeight * 0.55f) // Adjust these!
                .size(screenWidth * 0.5f)
                .clickable { isLampOn = !isLampOn }
        )

        // --- LAYER 3: BOOKS & CAT 📚🐱 ---
        // The Book Stack
        Image(
            painter = painterResource(id = R.drawable.prop_books),
            contentDescription = "Books",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = screenWidth * 0.24f, y = screenHeight * 0.63f)
                .size(screenWidth * 0.33f)
        )

        // The Cat (Sitting on books)
        Image(
            painter = painterResource(id = R.drawable.prop_cat),
            contentDescription = "Cat",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = screenWidth * 0.253f, y = screenHeight * 0.581f) // Slightly above books
                .size(screenWidth * 0.3f)
                .clickable {
                    // TODO: Play Meow Sound
                }
        )

        // --- LAYER 4: THE NOTEBOOK (Todo) 📝 ---
        Image(
            painter = painterResource(id = R.drawable.prop_notebook),
            contentDescription = "Notebook",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = screenWidth * 0.22f, y = screenHeight * 0.7f)
                .size(screenWidth * 0.4f)
                .clickable { showNotebook = true }
        )

        // --- LAYER 5: THE LAPTOP 💻 ---
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = screenHeight * 0.2f,x=screenWidth*0.3f) // Move down a bit
                .size(screenWidth * 0.45f, screenWidth * 0.4125f) // Adjust aspect ratio
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
                    .offset(y = (-20).dp), // Nudge up to fit in screen area
                contentAlignment = Alignment.Center
            ) {
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    color = Color.Green, // Hacker style?
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        // --- LAYER 6: UI CONTROLS (Back Button) 🔙 ---
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black.copy(alpha = 0.3f) // Semi-transparent black
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter) // Place at the bottom
                .padding(bottom = 40.dp)       // Give it some space from the edge
        ) {
            Text(
                text = "▼ Return to Room",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
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
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    SaveKittyTheme {
        TimerScreen(
            timeLeft = 20,
            isTimerRunning = true,
            onLaptopClick = {},
            onBackClick = {},
            todoList = emptyList(),
            onAddTodo = {},
            onToggleTodo = {},
            onDeleteTodo = {}

        )
    }

}

