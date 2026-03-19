package com.example.savekitty.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Calendar

object GameRepository {
    private var storage: GameStorage? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Game State
    private val _biscuits = MutableStateFlow(100)
    val biscuits = _biscuits.asStateFlow()
    private val _health = MutableStateFlow(5)
    val health = _health.asStateFlow()
    private val _fishCount = MutableStateFlow(0)
    val fishCount = _fishCount.asStateFlow()
    private val _timeLeft = MutableStateFlow(25 * 60)
    val timeLeft = _timeLeft.asStateFlow()
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()
    
    private val _timerFinishedEvent = kotlinx.coroutines.channels.Channel<Int>(kotlinx.coroutines.channels.Channel.BUFFERED)
    val timerFinishedEvent = _timerFinishedEvent.receiveAsFlow()
    
    private var currentSessionMinutes = 25

    private val _todoList = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoList = _todoList.asStateFlow()
    private val _history = MutableStateFlow<List<StudySession>>(emptyList())
    val history = _history.asStateFlow()
    private val _inventory = MutableStateFlow<Map<String, Int>>(emptyMap())
    val inventory = _inventory.asStateFlow()
    private val _catSkin = MutableStateFlow(0)
    val catSkin = _catSkin.asStateFlow()
    private val _deceasedCats = MutableStateFlow<List<DeceasedCat>>(emptyList())
    val deceasedCats = _deceasedCats.asStateFlow()
    private val _placedItems = MutableStateFlow<Map<DecorationType, String>>(emptyMap())
    val placedItems = _placedItems.asStateFlow()
    private val _catName = MutableStateFlow("")
    val catName = _catName.asStateFlow()
    private val _isTutorialComplete = MutableStateFlow(false)
    val isTutorialComplete = _isTutorialComplete.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    suspend fun initialize(context: Context) {
        storage = GameStorage(context)

        // 1. Load all state from disk first
        _catName.value = storage?.catNameFlow?.first() ?: ""
        _catSkin.value = storage?.catSkinFlow?.first() ?: 0
        _isTutorialComplete.value = storage?.isTutorialCompleteFlow?.first() ?: false
        _deceasedCats.value = storage?.deceasedCatsFlow?.first() ?: emptyList()
        _biscuits.value = storage?.biscuitsFlow?.first() ?: 100
        _health.value = storage?.healthFlow?.first() ?: 5
        _fishCount.value = storage?.fishFlow?.first() ?: 0
        _todoList.value = storage?.todoListFlow?.first() ?: emptyList()
        _history.value = storage?.historyFlow?.first() ?: emptyList()
        _inventory.value = storage?.inventoryFlow?.first() ?: emptyMap()
        _placedItems.value = storage?.placedItemsFlow?.first() ?: emptyMap()

        // 2. Perform startup logic with the loaded state

        // --- Offline Health Drain ---
        if (_catName.value.isNotEmpty()) {
            val lastTime = storage?.lastHealthTimeFlow?.first() ?: 0L
            val currentTime = System.currentTimeMillis()

            if (lastTime > 0) {
                val diffMillis = currentTime - lastTime
                if (diffMillis > 0) {
                    val hoursPassed = diffMillis / (1000 * 60 * 60)
                    val healthToLose = (hoursPassed / 6).toInt()

                    if (healthToLose > 0) {
                        val newHealth = (_health.value - healthToLose).coerceAtLeast(0)
                        _health.value = newHealth // Update in-memory
                        storage?.saveHealth(newHealth) // Save to disk

                        val timeAccountedFor = healthToLose * 6 * 60 * 60 * 1000L
                        storage?.saveLastHealthTime(lastTime + timeAccountedFor)
                    }
                }
            } else {
                storage?.saveLastHealthTime(currentTime)
            }
        }

        // --- Daily Todo Reset ---
        val lastDate = storage?.lastOpenDateFlow?.first() ?: 0L
        val today = System.currentTimeMillis()
        if (!isSameDay(lastDate, today)) {
            val resetList = _todoList.value.map { if (it.isDaily) it.copy(isDone = false) else it }
            updateAndSaveTodo(resetList)
            storage?.saveLastOpenDate(today)
        }

        // --- Offline Timer --- //
        val savedEndTime = storage?.timerEndTimeFlow?.first() ?: 0L
        if (savedEndTime > 0) {
            val currentTime = System.currentTimeMillis()
            if (savedEndTime > currentTime) {
                _timeLeft.value = ((savedEndTime - currentTime) / 1000).toInt()
                _isTimerRunning.value = true
                startTicking()
            } else {
                _timeLeft.value = 0
                _isTimerRunning.value = false
                storage?.saveTimerEndTime(0)
                _timerFinishedEvent.trySend(currentSessionMinutes)
            }
        }
    }

    fun setCatIdentity(name: String, skin: Int) {
        _catName.value = name
        _catSkin.value = skin
        _health.value = 10 // New cat starts fresh

        scope.launch {
            storage?.saveCatIdentity(name, skin)
            storage?.saveHealth(10)
            storage?.saveLastHealthTime(System.currentTimeMillis()) // Reset health drain timer
        }
    }

    fun handleGameOver() {
        if (_catName.value.isEmpty()) return // Safeguard

        val deadName = _catName.value
        val deadSkin = _catSkin.value

        scope.launch {
            val currentList = _deceasedCats.value
            val newList = currentList + DeceasedCat(deadName, deadSkin, System.currentTimeMillis())
            storage?.saveDeceasedCats(newList)
            _deceasedCats.value = newList

            _catName.value = ""
            storage?.saveCatIdentity("", 0)
        }
    }

    // Other functions remain largely the same...

    private fun startTicking() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (_isTimerRunning.value && _timeLeft.value > 0) {
                kotlinx.coroutines.delay(1000)
                _timeLeft.value -= 1
                if (_timeLeft.value <= 0) {
                    _isTimerRunning.value = false
                    storage?.saveTimerEndTime(0)
                    _timerFinishedEvent.trySend(currentSessionMinutes)
                }
            }
        }
    }

    private fun updateAndSaveTodo(newList: List<TodoItem>) {
        _todoList.value = newList
        scope.launch { storage?.saveTodoList(newList) }
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) pauseTimer() else startTimer()
    }

    private fun startTimer() {
        if (_timeLeft.value <= 0) return
        _isTimerRunning.value = true
        val endTime = System.currentTimeMillis() + (_timeLeft.value * 1000L)
        scope.launch { storage?.saveTimerEndTime(endTime) }
        startTicking()
    }

    private fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        scope.launch { storage?.saveTimerEndTime(0) }
    }

    fun setTimer(minutes: Int) {
        currentSessionMinutes = minutes
        _timeLeft.value = minutes * 60
        _isTimerRunning.value = false
        timerJob?.cancel()
        scope.launch { storage?.saveTimerEndTime(0) }
    }

    fun earnBiscuits(amount: Int) {
        val newValue = _biscuits.value + amount
        _biscuits.value = newValue
        scope.launch { storage?.saveBiscuits(newValue) }
    }

    fun spendBiscuits(amount: Int): Boolean {
        if (_biscuits.value >= amount) {
            val newValue = _biscuits.value - amount
            _biscuits.value = newValue
            scope.launch { storage?.saveBiscuits(newValue) }
            return true
        }
        return false
    }

    fun buyFish() {
        if (spendBiscuits(5)) {
            val newFish = _fishCount.value + 1
            _fishCount.value = newFish
            scope.launch { storage?.saveFish(newFish) }
        }
    }

    fun eatFish() {
        if (_fishCount.value > 0 && _health.value < 10) {
            val newFish = _fishCount.value - 1
            val newHealth = _health.value + 1
            _fishCount.value = newFish
            _health.value = newHealth
            scope.launch {
                storage?.saveFish(newFish)
                storage?.saveHealth(newHealth)
            }
        }
    }

    fun addTodo(text: String, isDaily: Boolean) {
        if (text.isBlank()) return
        val newItem = TodoItem(text = text, isDaily = isDaily)
        updateAndSaveTodo(_todoList.value + newItem)
    }

    fun toggleTodo(itemId: Long) {
        val newList = _todoList.value.map {
            if (it.id == itemId) it.copy(isDone = !it.isDone) else it
        }
        updateAndSaveTodo(newList)
    }

    fun deleteTodo(itemId: Long) {
        updateAndSaveTodo(_todoList.value.filter { it.id != itemId })
    }

    fun recordSession(minutes: Int) {
        val newSession = StudySession(durationMinutes = minutes)
        val newList = _history.value + newSession
        _history.value = newList
        scope.launch { storage?.saveHistory(newList) }
    }

    fun buyFood(food: Food) {
        if (spendBiscuits(food.price)) {
            val newInventory = _inventory.value.toMutableMap()
            newInventory[food.id] = (_inventory.value[food.id] ?: 0) + 1
            _inventory.value = newInventory
            scope.launch { storage?.saveInventory(newInventory) }
        }
    }

    fun eatFood(food: Food) {
        val currentCount = _inventory.value[food.id] ?: 0
        if (currentCount > 0 && _health.value < 10) {
            val newInventory = _inventory.value.toMutableMap()
            newInventory[food.id] = currentCount - 1
            val newHealth = (_health.value + food.healthPoints).coerceAtMost(10)
            _inventory.value = newInventory
            _health.value = newHealth
            scope.launch {
                storage?.saveInventory(newInventory)
                storage?.saveHealth(newHealth)
            }
        }
    }

    fun buyDecoration(item: Decoration) {
        if ((_inventory.value[item.id] ?: 0) == 0) {
            if (spendBiscuits(item.price)) {
                val newInventory = _inventory.value.toMutableMap().apply { this[item.id] = 1 }
                _inventory.value = newInventory
                scope.launch { storage?.saveInventory(newInventory) }
            }
        }
    }

    fun equipDecoration(item: Decoration) {
        if ((_inventory.value[item.id] ?: 0) > 0) {
            val newPlacedItems = _placedItems.value.toMutableMap().apply { this[item.type] = item.id }
            _placedItems.value = newPlacedItems
            scope.launch { storage?.savePlacedItems(newPlacedItems) }
        }
    }

    fun completeTutorial() {
        _isTutorialComplete.value = true
        scope.launch { storage?.saveIsTutorialComplete(true) }
    }

    private fun isSameDay(t1: Long, t2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
