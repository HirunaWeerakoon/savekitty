package com.example.savekitty.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savekitty.data.StudySession
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(
    history: List<StudySession>,
    onBackClick: () -> Unit
) {
    // 1. Calculate Stats
    val totalSessions = history.size
    val totalMinutes = history.sumOf { it.durationMinutes }
    val totalHours = String.format("%.1f", totalMinutes / 60f)

    val pixelFont = FontFamily.Monospace

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF5D4037)) // Wood/Desk Background
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "STUDY LOG",
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = pixelFont,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- BIG STATS CARDS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("SESSIONS", "$totalSessions")
            StatCard("HOURS", totalHours)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- THE GRAPH (Last 7 Days) ---
        Text(
            text = "LAST 7 DAYS",
            color = Color(0xFFFFD54F),
            fontFamily = pixelFont,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            WeeklyGraph(history)
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp)) // Paper Color
            .padding(24.dp)
    ) {
        Text(value, fontSize = 32.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Black)
        Text(label, fontSize = 14.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun WeeklyGraph(history: List<StudySession>) {
    // 1. Aggregate data by day (0 = Today, 1 = Yesterday...)
    val today = Calendar.getInstance()
    val dailyMinutes = IntArray(7) { 0 } // Stores mins for last 7 days

    history.forEach { session ->
        val sessionDate = Calendar.getInstance().apply { timeInMillis = session.timestamp }
        val diffMillis = today.timeInMillis - sessionDate.timeInMillis
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

        if (diffDays in 0..6) {
            dailyMinutes[6 - diffDays] += session.durationMinutes // Store in array
        }
    }

    // Find max for scaling
    val maxVal = dailyMinutes.maxOrNull() ?: 1
    val safeMax = if (maxVal == 0) 60 else maxVal

    // 2. Draw Bars
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S") // Simplified labels

        // (In a real app, calculate actual day names based on 'today')

        for (i in 0..6) {
            val heightRatio = dailyMinutes[i].toFloat() / safeMax.toFloat()
            val barHeight = 200.dp * heightRatio // Max height 200dp

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // The Bar
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(if (barHeight < 4.dp) 4.dp else barHeight) // Min height
                        .background(Color(0xFF81C784)) // Retro Green
                )
                Spacer(modifier = Modifier.height(8.dp))
                // The Label
                Text(
                    text = "${dailyMinutes[i] / 60}h", // Simple label (e.g. 1h)
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}