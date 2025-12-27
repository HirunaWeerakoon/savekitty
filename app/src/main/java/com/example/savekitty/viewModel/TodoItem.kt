package com.example.savekitty.viewModel

data class TodoItem(
    val id: Long = System.currentTimeMillis(), // Unique ID based on time
    val text: String,
    val isDone: Boolean = false
)