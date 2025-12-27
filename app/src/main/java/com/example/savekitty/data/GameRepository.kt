package com.example.savekitty.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// A Singleton Repository that holds the "Truth" of the game state.
// In a real app, this would save to a Database (Room) or DataStore.
object GameRepository {

    // 1. GAME STATE
    private val _coins = MutableStateFlow(100)
    val coins = _coins.asStateFlow()

    private val _health = MutableStateFlow(5) // 0 to 10 scale
    val health = _health.asStateFlow()

    private val _fishCount = MutableStateFlow(0)
    val fishCount = _fishCount.asStateFlow()

    private val _timeLeft = MutableStateFlow(25 * 60) // Seconds
    val timeLeft = _timeLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    // 2. ACTIONS
    fun toggleTimer() {
        _isTimerRunning.update { !it }
    }

    fun setTimer(minutes: Int) {
        _timeLeft.value = minutes * 60
        _isTimerRunning.value = false
    }
    fun tickTimer() {
        if (_isTimerRunning.value && _timeLeft.value > 0) {
            _timeLeft.update { it - 1 }
        } else if (_timeLeft.value == 0) {
            _isTimerRunning.value = false
        }
    }
    fun earnCoins(amount: Int) {
        _coins.update { it + amount }
    }

    fun spendCoins(amount: Int): Boolean {
        if (_coins.value >= amount) {
            _coins.update { it - amount }
            return true
        }
        return false
    }

    fun addFish(amount: Int) {
        _fishCount.update { it + amount }
    }

    fun eatFish() {
        if (_fishCount.value > 0 && _health.value < 10) {
            _fishCount.update { it - 1 }
            _health.update { it + 1 }
        }
    }
}