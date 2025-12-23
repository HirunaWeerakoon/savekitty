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

    private val _fishCount = MutableStateFlow(0) // Start with 0 fish
    val fishCount = _fishCount.asStateFlow()

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
    fun completeStudySession() {
        _coins.update { it + 10 }
    }

    // Called in the Shop
    fun buyFish() {
        if (_coins.value >= 5) {
            _coins.update { it - 5 }
            _fishCount.update { it + 1 }
        }
    }

    // Called when dragging fish to cat's mouth
    fun consumeFish() {
        // Only eat if we have fish AND not full health
        if (_fishCount.value > 0 && _health.value < 10) {
            _fishCount.update { it - 1 }
            _health.update { it + 1 } // +1 health (Half Heart)
        }
    }
}