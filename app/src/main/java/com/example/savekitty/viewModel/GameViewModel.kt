package com.example.savekitty.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.savekitty.R
import com.example.savekitty.data.Decoration
import com.example.savekitty.data.Food
import com.example.savekitty.data.GameRepository
import com.example.savekitty.data.NotificationHelper
import com.example.savekitty.data.NotificationWorker
import com.example.savekitty.data.SoundManager
import com.example.savekitty.data.TodoItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class GameViewModel : ViewModel() {

    // --- REPOSITORY STATE ---
    val health = GameRepository.health
    val biscuits = GameRepository.biscuits
    val fishCount = GameRepository.fishCount
    val todoList = GameRepository.todoList
    val history = GameRepository.history
    val inventory = GameRepository.inventory

    // --- LOCAL VIEWMODEL STATE ---
    private var appContext: Context? = null
    private var soundManager: SoundManager? = null
    private var notificationHelper: NotificationHelper? = null

    private val _isMuted = MutableStateFlow(false)
    val isMutedState = _isMuted.asStateFlow()

    // TIMER STATE
    private val _timeLeft = MutableStateFlow(25 * 60) // Default 25 min (1500 seconds)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    // Memory for resetting (Defaults to 25m)
    private var lastSelectedTime = 25 * 60

    private var timerJob: Job? = null

    val isFirstRun = GameRepository.isFirstRun
    val deceasedCats = GameRepository.deceasedCats
    val placedItems = GameRepository.placedItems
    // In GameViewModel class

    // --- TIMER LOGIC ---

    fun setTime(seconds: Int) {
        // Critical Fix: Only update if timer is stopped
        if (!_isTimerRunning.value) {
            _timeLeft.value = seconds
            lastSelectedTime = seconds // Remember: 1500 or 3000
        }
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return

        _isTimerRunning.value = true

        timerJob = viewModelScope.launch {
            // Count down while time remains
            while (_timeLeft.value > 0 && _isTimerRunning.value) {
                delay(1000L)
                _timeLeft.value -= 1
            }

            // CHECK: Did we finish naturally? (Time hit 0)
            if (_timeLeft.value == 0) {
                onTimerFinished() // Trigger Rewards
            }

            // Ensure clean stop state
            stopTimer()
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        // RESET LOGIC: Snap back to the last selected time (e.g. 50:00)
        _timeLeft.value = lastSelectedTime
    }

    private fun onTimerFinished() {
        // ALL REWARD LOGIC LIVES HERE NOW
        soundManager?.playLevelUp()
        notificationHelper?.showTimerComplete()

        // Calculate minutes worked based on what the timer started at
        val minutesFocus = lastSelectedTime / 60
        GameRepository.recordSession(minutesFocus)

        // Reward: 10 biscuits for 25m, maybe 20 for 50m?
        // For now, let's keep it simple:
        completeStudySession()
    }

    // --- SOUND & GAME ACTIONS ---

    fun setContext(context: Context) {
        this.appContext = context.applicationContext
    }

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

    fun setNotificationHelper(helper: NotificationHelper) {
        this.notificationHelper = helper
    }

    fun buyFish() {
        if (GameRepository.biscuits.value >= 5) {
            GameRepository.buyFish()
            soundManager?.playChing()
        }
    }

    fun consumeFish() = GameRepository.eatFish()

    fun onCatClick(isSleeping: Boolean) {
        GameRepository.earnBiscuits(1)
        if (isSleeping) {
            // SLEEPING: Purr + Vibrate
            soundManager?.playPurr()
            vibratePhone(3000)
        } else {
            // HUNGRY: Meow only
            soundManager?.playMeow()
        }
    }
    private fun vibratePhone(durationMs: Long) {
        val vibrator = appContext?.getSystemService(Context.VIBRATOR_SERVICE) as? android.os.Vibrator
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            // Use the duration here
            vibrator?.vibrate(android.os.VibrationEffect.createOneShot(durationMs, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // Use the duration here
            vibrator?.vibrate(durationMs)
        }
    }

    fun startMeowTest() {
        viewModelScope.launch {
            delay(10000)
            notificationHelper?.showMeowNotification()
            soundManager?.playMeow()
        }
    }

    fun onAppBackgrounded() {
        viewModelScope.launch {
            soundManager?.pauseMusic()
            scheduleNotification()
        }
    }

    fun onAppPause() {
        soundManager?.pauseMusic()
    }

    fun onAppResume() {
        soundManager?.resumeMusic()
        cancelNotification()
    }
    fun setCatIdentity(name: String, skin: Int) {
        GameRepository.setCatIdentity(name, skin)
    }

    fun handleGameOver() {
        GameRepository.handleGameOver()
    }

    // --- TODO & DATA ---

    fun addTodo(text: String, isDaily: Boolean) = GameRepository.addTodo(text, isDaily)
    fun toggleTodo(id: Long) = GameRepository.toggleTodo(id)
    fun deleteTodo(id: Long) = GameRepository.deleteTodo(id)
    fun buyFood(food: Food) = GameRepository.buyFood(food)
    fun eatFood(food: Food) {
        // ... existing logic ...
        GameRepository.eatFood(food)
        soundManager?.playEat()
    }
    // --- WORKER / NOTIFICATIONS ---

    private fun scheduleNotification() {
        val context = appContext ?: return
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(24, TimeUnit.HOURS)
            .addTag("meow_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "meow_reminder_work",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelNotification() {
        val context = appContext ?: return
        WorkManager.getInstance(context).cancelAllWorkByTag("meow_reminder")
    }
    fun buyDecoration(item: Decoration) = GameRepository.buyDecoration(item)
    fun equipDecoration(item: Decoration) = GameRepository.equipDecoration(item)

}
