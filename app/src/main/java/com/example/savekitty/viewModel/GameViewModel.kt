package com.example.savekitty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.savekitty.data.GameRepository
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

    private val _todoList = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoList = _todoList.asStateFlow()

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

    fun setTimer(minutes: Int) = GameRepository.setTimer(minutes)

    // 2. Actions (Delegate to Repository)
    fun completeStudySession() {
        GameRepository.earnCoins(10)
    }

    fun buyFish() {
        val success = GameRepository.spendCoins(5)
        if (success) {
            GameRepository.addFish(1)
        }
    }

    fun consumeFish() {
        GameRepository.eatFish()
    }

    fun onCatClick() {
        GameRepository.earnCoins(1)
    }
    fun addTodo(text: String) {
        if (text.isBlank()) return
        val newItem = TodoItem(text = text)
        _todoList.update { currentList ->
            currentList + newItem
        }
    }

    fun toggleTodo(itemId: Long) {
        _todoList.update { currentList ->
            currentList.map { item ->
                if (item.id == itemId) item.copy(isDone = !item.isDone) else item
            }
        }
    }

    fun deleteTodo(itemId: Long) {
        _todoList.update { currentList ->
            currentList.filter { it.id != itemId }
        }
    }
}