package com.example.savekitty.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TutorialOverlay(
    onFinish: () -> Unit
) {
    // 0: Intro, 1: Health, 2: Timer, 3: Shop, 4: Finish
    var step by remember { mutableIntStateOf(0) }

    val titles = listOf(
        "Welcome to SaveKitty!",
        "Keep Kitty Alive",
        "Earn Biscuits",
        "Decorate & Feed",
        "Ready?"
    )

    val messages = listOf(
        "This is your new study companion. Let's learn how to take care of them.",
        "Your cat loses health over time. If hearts reach 0, they leave! Feed them regularly.",
        "Tap the Laptop or Table to start a Focus Timer. Real study time = Biscuits earned!",
        "Use Biscuits in the Shop to buy food or cool decorations for your room.",
        "Good luck! Study hard and save the kitty."
    )

    // A dark overlay that blocks clicks to the game behind it
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(enabled = true) { /* Consume clicks so game doesn't react */ },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .background(Color.DarkGray, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titles[step],
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = messages[step],
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (step < titles.size - 1) {
                        step++
                    } else {
                        onFinish()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = if (step < titles.size - 1) "Next" else "Let's Go!",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}