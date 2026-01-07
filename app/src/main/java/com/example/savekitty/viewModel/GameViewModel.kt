package com.example.savekitty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savekitty.R
import com.example.savekitty.data.Food
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.savekitty.data.GameRepository
import com.example.savekitty.data.SoundManager
import com.example.savekitty.data.TodoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.savekitty.data.NotificationHelper
import kotlinx.coroutines.flow.asStateFlow
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import java.util.concurrent.TimeUnit
import com.example.savekitty.data.NotificationWorker
import android.content.Context
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow


class GameViewModel : ViewModel() {

    // 1. GAME STATE (Private so only this class can change it)
    // Start with 1 health (Half Heart) for testing
    val health = GameRepository.health
    val biscuits = GameRepository.biscuits
    val fishCount = GameRepository.fishCount


    val todoList = GameRepository.todoList

    val history = GameRepository.history
    val inventory = GameRepository.inventory
    private var appContext: Context? = null

    private val _isMuted = MutableStateFlow(false)
    val isMutedState = _isMuted.asStateFlow()

    private var soundManager: SoundManager? = null
    private var notificationHelper: NotificationHelper? = null

    val PixelFontStyle = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )

    private val _todoList = MutableStateFlow<List<TodoItem>>(emptyList())

    private val _timeLeft = MutableStateFlow(25 * 60) // Default 25 min
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    // New: Remember what the user last picked so we can reset to it
    private var lastSelectedTime = 25 * 60

    private var timerJob: Job? = null



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
    fun setContext(context: Context) {
        this.appContext = context.applicationContext
    }
    fun setTime(seconds: Int) {
        // Only allow changing time if timer is NOT running
        if (!_isTimerRunning.value) {
            _timeLeft.value = seconds
            lastSelectedTime = seconds // Remember this!
        }
    }

    fun toggleTimer() {
        // ERROR FIX: Access .value to check the boolean
        if (_isTimerRunning.value) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    // Helper: Start the Countdown
    private fun startTimer() {
        // Prevent multiple timers
        if (timerJob?.isActive == true) return

        _isTimerRunning.value = true

        // Launch a coroutine on the ViewModel's scope
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0 && _isTimerRunning.value) {
                delay(1000L) // Wait 1 second
                _timeLeft.value -= 1 // Decrease time
            }
            // When loop finishes (time hits 0), stop cleanly
            if (_timeLeft.value == 0) {
                stopTimer()
            }
        }
    }

    // Helper: Stop and Reset
    private fun stopTimer() {
        timerJob?.cancel() // Kill the coroutine
        _isTimerRunning.value = false
        _timeLeft.value = lastSelectedTime // RESET logic: Go back to 25m or 50m
    }
    fun setTimer(m: Int) = GameRepository.setTimer(m)

    // 2. Actions (Delegate to Repository)
    fun completeStudySession() {
        GameRepository.earnBiscuits(10)
    }
    fun setSoundManager(manager: SoundManager) {
        this.soundManager = manager
        soundManager?.playMusic(R.raw.music_lofi_beat)
        _isMuted.value = manager.isMuted
    }
    fun playDoorSound() = soundManager?.playDoorOpen()
    fun playNotebookSound() = soundManager?.playNotebookFlip()
    fun playClickSound() = soundManager?.playButtonPress()

    fun toggleMute() {
        soundManager?.toggleMute()
        _isMuted.value = soundManager?.isMuted == true
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
            soundManager?.pauseMusic()
            scheduleNotification()

        }
    }
    fun addTodo(text: String, isDaily: Boolean) {
        GameRepository.addTodo(text, isDaily)
    }
    fun toggleTodo(id: Long) = GameRepository.toggleTodo(id)
    fun deleteTodo(id: Long) = GameRepository.deleteTodo(id)

    fun buyFood(food: Food) = GameRepository.buyFood(food)
    fun eatFood(food: Food) = GameRepository.eatFood(food)

    fun onAppPause() {
        soundManager?.pauseMusic()
    }

    fun onAppResume() {
        soundManager?.resumeMusic()
        cancelNotification()
    }
    private fun scheduleNotification() {
        val context = appContext ?: return

        // 1. Create a request to run the Worker in 24 HOURS
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(24, TimeUnit.HOURS) // <--- CHANGE THIS TO 10, TimeUnit.SECONDS TO TEST!
            .addTag("meow_reminder") // Give it a name tag so we can find it later
            .build()

        // 2. Enqueue it (REPLACE ensures we don't have duplicate timers)
        WorkManager.getInstance(context).enqueueUniqueWork(
            "meow_reminder_work",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    private fun cancelNotification() {
        val context = appContext ?: return
        // User came back! Cancel the pending notification.
        WorkManager.getInstance(context).cancelAllWorkByTag("meow_reminder")
    }




}