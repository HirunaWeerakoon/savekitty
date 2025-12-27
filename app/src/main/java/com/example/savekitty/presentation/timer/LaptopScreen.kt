package com.example.savekitty.presentation.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LaptopScreen(
    timeLeft: Int,
    isTimerRunning: Boolean,
    onToggleTimer: () -> Unit,
    onSetTime: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    // We simulate the Laptop Screen UI here
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2E)), // Dark Blue/Black "Screen" color
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. The Big Timer
        val minutes = timeLeft / 60
        val seconds = timeLeft % 60
        Text(
            text = "%02d:%02d".format(minutes, seconds),
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Control Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onToggleTimer,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(if (isTimerRunning) "PAUSE" else "START", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Time Presets
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { onSetTime(25) }) { Text("25m") }
            Button(onClick = { onSetTime(50) }) { Text("50m") }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // 4. Leave Computer (Back)
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Leave Desk (Back)")
        }
    }
}