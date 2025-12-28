package com.example.savekitty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.savekitty.data.GameRepository
import com.example.savekitty.data.TodoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay



class GameViewModel : ViewModel() {

    // 1. GAME STATE (Private so only this class can change it)
    // Start with 1 health (Half Heart) for testing
    val health = GameRepository.health
    val coins = GameRepository.coins
    val fishCount = GameRepository.fishCount
    val timeLeft = GameRepository.timeLeft
    val isTimerRunning = GameRepository.isTimerRunning
    val todoList = GameRepository.todoList

    val PixelFontStyle = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )

    private val _todoList = MutableStateFlow<List<TodoItem>>(emptyList())


    init {
        // Start the Heartbeat of the Timer
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                GameRepository.tickTimer()
            }
        }
    }
    fun toggleTimer() = GameRepository.toggleTimer()
    fun setTimer(m: Int) = GameRepository.setTimer(m)

    // 2. Actions (Delegate to Repository)
    fun completeStudySession() {
        GameRepository.earnCoins(10)
    }

    fun buyFish() = GameRepository.buyFish()
    fun consumeFish() = GameRepository.eatFish()

    fun onCatClick() {
        GameRepository.earnCoins(1)
    }
    fun addTodo(text: String) = GameRepository.addTodo(text)

    fun toggleTodo(id: Long) = GameRepository.toggleTodo(id)
    fun deleteTodo(id: Long) = GameRepository.deleteTodo(id)

}