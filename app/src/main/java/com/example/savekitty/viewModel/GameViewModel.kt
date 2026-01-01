package com.example.savekitty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.savekitty.data.GameRepository
import com.example.savekitty.data.SoundManager
import com.example.savekitty.data.TodoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.savekitty.data.NotificationHelper



class GameViewModel : ViewModel() {

    // 1. GAME STATE (Private so only this class can change it)
    // Start with 1 health (Half Heart) for testing
    val health = GameRepository.health
    val biscuits = GameRepository.biscuits
    val fishCount = GameRepository.fishCount
    val timeLeft = GameRepository.timeLeft
    val isTimerRunning = GameRepository.isTimerRunning
    val todoList = GameRepository.todoList

    val history = GameRepository.history

    private var soundManager: SoundManager? = null
    private var notificationHelper: NotificationHelper? = null

    val PixelFontStyle = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )

    private val _todoList = MutableStateFlow<List<TodoItem>>(emptyList())



    init {
        // Start the Heartbeat of the Timer 💓
        viewModelScope.launch {
            while (true) {
                delay(1000L) // Wait 1 second

                // 1. Capture state BEFORE the tick
                val timeBefore = timeLeft.value
                val wasRunning = isTimerRunning.value

                // 2. Tick the timer
                GameRepository.tickTimer()

                // 3. Check if it JUST finished
                // (It was running, it had time left, and NOW it is 0)
                if (wasRunning && timeBefore > 0 && timeLeft.value == 0) {
                    soundManager?.playLevelUp()       // 🔊 DING!
                    notificationHelper?.showTimerComplete() // 🔔 Notification
                    GameRepository.recordSession(25)
                    completeStudySession()            // 🍪 Reward (+10 Biscuits)
                }
            }
        }
    }
    fun toggleTimer() = GameRepository.toggleTimer()
    fun setTimer(m: Int) = GameRepository.setTimer(m)

    // 2. Actions (Delegate to Repository)
    fun completeStudySession() {
        GameRepository.earnBiscuits(10)
    }
    fun setSoundManager(manager: SoundManager) {
        this.soundManager = manager
    }
    fun setNotificationHelper(helper: NotificationHelper) { this.notificationHelper = helper }

    fun buyFish() {
        // We check if the purchase was successful first
        if (GameRepository.biscuits.value >= 5) { // Check repository value
            GameRepository.buyFish() // Perform logic
            soundManager?.playChing() // <--- KA-CHING! 💰
        }
    }
    fun consumeFish() = GameRepository.eatFish()

    fun onCatClick() {
        GameRepository.earnBiscuits(1)
        soundManager?.playMeow()
    }
    fun startMeowTest() {
        viewModelScope.launch {
            // Wait 10 seconds
            delay(10000)
            // Send the notification
            notificationHelper?.showMeowNotification()
            // Optional: Play sound too
            soundManager?.playMeow()
        }
    }
    fun onAppBackgrounded() {
        viewModelScope.launch {
            // Wait 10 seconds (change to 600000 for 10 minutes later)
            delay(10_000)

            // Send the notification
            notificationHelper?.showMeowNotification()

            // Optional: Log it so you know it ran
            println("Meow notification sent!")
        }
    }
    fun addTodo(text: String) = GameRepository.addTodo(text)

    fun toggleTodo(id: Long) = GameRepository.toggleTodo(id)
    fun deleteTodo(id: Long) = GameRepository.deleteTodo(id)

}