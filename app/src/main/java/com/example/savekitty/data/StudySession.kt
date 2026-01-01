package com.example.savekitty.data

data class StudySession(
    val timestamp: Long = System.currentTimeMillis(), // When did it finish?
    val durationMinutes: Int // How long was it?
)