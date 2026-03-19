package com.example.savekitty.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

class GameViewModel(application: Application) : AndroidViewModel(application) {

    // --- REPOSITORY STATE ---
    val health = GameRepository.health
    val biscuits = GameRepository.biscuits
    val fishCount = GameRepository.fishCount
    val todoList = GameRepository.todoList
    val history = GameRepository.history
    val inventory = GameRepository.inventory
    val isTutorialComplete = GameRepository.isTutorialComplete
    val catSkin = GameRepository.catSkin
    val catName = GameRepository.catName
    val deceasedCats = GameRepository.deceasedCats
    val placedItems = GameRepository.placedItems
    val timeLeft: StateFlow<Int> = GameRepository.timeLeft
    val isTimerRunning: StateFlow<Boolean> = GameRepository.isTimerRunning

    // --- VIEWMODEL STATE ---
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _showHospitalDialog = MutableStateFlow(false)
    val showHospitalDialog = _showHospitalDialog.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMutedState = _isMuted.asStateFlow()

    private var soundManager: SoundManager? = null
    private var notificationHelper: NotificationHelper? = null

    init {
        viewModelScope.launch {
            GameRepository.initialize(application.applicationContext)
            _isLoading.value = false // Signal that loading is complete
            
            launch {
                GameRepository.timerFinishedEvent.collect { minutes ->
                    onTimerFinished(minutes)
                }
            }
        }
    }

    fun onHospitalDialogDismissed() {
        _showHospitalDialog.value = false
    }

    fun setTime(seconds: Int) = GameRepository.setTimer(seconds / 60)
    fun toggleTimer() = GameRepository.toggleTimer()

    private fun onTimerFinished(minutesFocus: Int) {
        soundManager?.playLevelUp()
        notificationHelper?.showTimerComplete()
        GameRepository.recordSession(minutesFocus)
        completeStudySession()
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

    fun onCatClick(isSleeping: Boolean) {
        GameRepository.earnBiscuits(1)
        if (isSleeping) {
            soundManager?.playPurr()
        } else {
            soundManager?.playMeow()
        }
    }

    fun onAppPause() {
        soundManager?.pauseMusic()
    }

    fun onAppResume() {
        soundManager?.resumeMusic()
        cancelNotification()
    }

    fun onAppBackgrounded() {
        soundManager?.pauseMusic()
        scheduleNotification()
    }

    fun setCatIdentity(name: String, skin: Int) {
        GameRepository.setCatIdentity(name, skin)
    }

    fun handleGameOver() {
        GameRepository.handleGameOver()
        _showHospitalDialog.value = true
    }

    fun addTodo(text: String, isDaily: Boolean) = GameRepository.addTodo(text, isDaily)
    fun toggleTodo(id: Long) = GameRepository.toggleTodo(id)
    fun deleteTodo(id: Long) = GameRepository.deleteTodo(id)
    fun buyFood(food: Food) = GameRepository.buyFood(food)
    fun eatFood(food: Food) {
        GameRepository.eatFood(food)
        soundManager?.playEat()
    }

    private fun scheduleNotification() {
        val context = getApplication<Application>().applicationContext
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
        val context = getApplication<Application>().applicationContext
        WorkManager.getInstance(context).cancelAllWorkByTag("meow_reminder")
    }

    fun buyDecoration(item: Decoration) = GameRepository.buyDecoration(item)
    fun equipDecoration(item: Decoration) = GameRepository.equipDecoration(item)
    fun earnAdReward() {
        GameRepository.earnBiscuits(5)
        soundManager?.playChing()
    }

    fun completeTutorial() {
        GameRepository.completeTutorial()
    }

    fun completeStudySession() {
        GameRepository.earnBiscuits(10)
    }

    fun buyFish() {
        if (GameRepository.biscuits.value >= 5) {
            GameRepository.buyFish()
            soundManager?.playChing()
        }
    }

    fun consumeFish() = GameRepository.eatFish()
}
