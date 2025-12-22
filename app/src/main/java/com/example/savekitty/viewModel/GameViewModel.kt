package com.example.savekitty.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    // 1. GAME STATE (Private so only this class can change it)
    // Start with 1 health (Half Heart) for testing
    private val _health = MutableStateFlow(1)
    val health = _health.asStateFlow() // Public read-only version

    private val _coins = MutableStateFlow(100)
    val coins = _coins.asStateFlow()

    // 2. ACTIONS (Logic)
    fun feedKitty() {
        // Only feed if not already full (10 = 5 Hearts)
        if (_health.value < 10) {
            _health.update { it + 1 }
            // Optional: Reduce coins when feeding?
            // _coins.update { it - 10 }
        }
    }

    fun onCatClick() {
        // Logic for cat click (Meow sound, etc.)
        // For now, maybe earn a coin?
        _coins.update { it + 1 }
    }
}