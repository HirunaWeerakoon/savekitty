package com.example.savekitty.presentation.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider // FIXED: New import
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.savekitty.R
import com.example.savekitty.presentation.SpriteAnimation.SpriteAnimation
import com.example.savekitty.ui.theme.SaveKittyTheme

@Composable
fun LaptopScreen(
    timeLeft: Int,
    isTimerRunning: Boolean,
    onToggleTimer: () -> Unit, // REVERTED: Back to single toggle
    onSetTime: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    // Design System Colors
    val terminalGreen = Color(0xFF2E3333)
    val cream = Color(0xFFF8F8E9)
    val selectionGrey = Color(0xFFE0E0D0)
    val catAccent = Color(0xFFFF5555)

    val biscuitFrames = listOf(
        R.drawable.cat_knead_0,
        R.drawable.cat_knead_1,
        R.drawable.cat_knead_2,
        R.drawable.cat_knead_1
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cream)
            .systemBarsPadding()
    ) {

        // --- CENTER STACK ---
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            // --- LAYER 1: THE CAT ---
            Box(
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .size(140.dp)
                    .zIndex(2f)
            ) {
                if (isTimerRunning) {
                    SpriteAnimation(
                        frames = biscuitFrames,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.cat_knead_0),
                        contentDescription = "Cat Idle",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // --- LAYER 2: THE CARD ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                    .zIndex(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cream),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("FOCUS_TIMER.EXE", color = terminalGreen, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        Icon(Icons.Default.Close, null, tint = terminalGreen, modifier = Modifier.size(16.dp))
                    }

                    // FIXED: Used HorizontalDivider instead of Divider
                    HorizontalDivider(color = terminalGreen, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                    // THE BIG TIMER
                    Text(
                        text = formatTime(timeLeft),
                        fontSize = 72.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isTimerRunning) catAccent else terminalGreen
                    )

                    // TIME SELECTORS
                    AnimatedVisibility(
                        visible = !isTimerRunning,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TimeSelectionChip(
                                label = "25m",
                                onClick = { onSetTime(25 * 60) },
                                color = selectionGrey,
                                textColor = terminalGreen
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            TimeSelectionChip(
                                label = "50m",
                                onClick = { onSetTime(50 * 60) },
                                color = selectionGrey,
                                textColor = terminalGreen
                            )
                        }
                    }

                    if (isTimerRunning) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // START / STOP BUTTON
                    Button(
                        onClick = onToggleTimer, // Simple callback again
                        colors = ButtonDefaults.buttonColors(containerColor = terminalGreen),
                        modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
                    ) {
                        Icon(
                            if (isTimerRunning) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isTimerRunning) "STOP" else "START",
                            color = Color.LightGray,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        // --- BACK FOLDER ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 35.dp)
                .clickable(onClick = onBackClick),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_folder),
                contentDescription = "Back",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Back",
                color = terminalGreen, // Changed to Green to match theme
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.5f))
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

// ... helper functions remain the same ...
@Composable
fun TimeSelectionChip(
    label: String,
    onClick: () -> Unit,
    color: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = label, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = textColor)
    }
}

fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
@Preview(showBackground = true)
@Composable
fun LaptopScreenPreview() {
    SaveKittyTheme {
        LaptopScreen(
            timeLeft = 1500, // 25:00
            isTimerRunning = false,
            onToggleTimer = {},
            onSetTime = {},
            onBackClick = {}
        )
    }
}