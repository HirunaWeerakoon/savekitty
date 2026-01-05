package com.example.savekitty.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// A Singleton Repository that holds the "Truth" of the game state.
// In a real app, this would save to a Database (Room) or DataStore.
object GameRepository {
    private var storage: GameStorage? = null
    // A Scope for background tasks (Database writes)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // 1. GAME STATE
    private val _biscuits = MutableStateFlow(100)
    val biscuits = _biscuits.asStateFlow()

    private val _health = MutableStateFlow(5) // 0 to 10 scale
    val health = _health.asStateFlow()

    private val _fishCount = MutableStateFlow(0)
    val fishCount = _fishCount.asStateFlow()

    private val _timeLeft = MutableStateFlow(25 * 60) // Seconds
    val timeLeft = _timeLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private val _todoList = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoList = _todoList.asStateFlow()

    private val _history = MutableStateFlow<List<StudySession>>(emptyList())
    val history = _history.asStateFlow()

    fun initialize(context: Context) {
        storage = GameStorage(context)

        // Sync: When Disk changes -> Update Memory
        scope.launch {
            storage?.biscuitsFlow?.collectLatest { _biscuits.value = it }
        }
        scope.launch {
            storage?.healthFlow?.collectLatest { _health.value = it }
        }
        scope.launch {
            storage?.fishFlow?.collectLatest { _fishCount.value = it }
        }
        scope.launch {
            storage?.todoListFlow?.collectLatest { _todoList.value = it }
        }
        scope.launch {
            storage?.biscuitsFlow?.collectLatest { _biscuits.value = it }
        }
        scope.launch {
            storage?.historyFlow?.collectLatest { _history.value = it }
        }
    }

    // 2. ACTIONS
    fun toggleTimer() {
        _isTimerRunning.value = !_isTimerRunning.value
    }

    fun setTimer(minutes: Int) {
        _timeLeft.value = minutes * 60
        _isTimerRunning.value = false
    }
    fun tickTimer() {
        if (_isTimerRunning.value && _timeLeft.value > 0) {
            _timeLeft.value -= 1
        } else if (_timeLeft.value == 0) {
            _isTimerRunning.value = false
        }
    }
    fun earnBiscuits(amount: Int) {
        val newValue = _biscuits.value + amount
        // 2. Save to Disk (UI updates automatically via the Sync above)
        scope.launch { storage?.saveBiscuits(newValue) }
    }

    fun spendBiscuits(amount: Int): Boolean {
        if (_biscuits.value >= amount) {
            val newValue = _biscuits.value - amount
            scope.launch { storage?.saveBiscuits(newValue) }
            return true
        }
        return false
    }

    fun addFish(amount: Int) {
        val newValue = _fishCount.value + amount
        scope.launch { storage?.saveFish(newValue) }
    }
    fun buyFish() {
        // Logic: Try to spend 5 biscuits. If successful, add 1 fish.
        if (spendBiscuits(5)) {
            addFish(1)
        }
    }

    fun eatFish() {
        if (_fishCount.value > 0 && _health.value < 10) {
            val newFish = _fishCount.value - 1
            val newHealth = _health.value + 1

            scope.launch {
                storage?.saveFish(newFish)
                storage?.saveHealth(newHealth)
            }
        }
    }
    fun addTodo(text: String) {
        if (text.isBlank()) return
        val newItem = TodoItem(
            text = text,
            isDaily = isDaily
        )
        val newList = _todoList.value + newItem
        updateAndSaveTodo(newList)
    }

    fun toggleTodo(itemId: Long) {
        val newList = _todoList.value.map { item ->
            if (item.id == itemId) item.copy(isDone = !item.isDone) else item
        }
        updateAndSaveTodo(newList)
    }

    fun deleteTodo(itemId: Long) {
        val newList = _todoList.value.filter { it.id != itemId }
        updateAndSaveTodo(newList)
    }

    // Helper to save to disk
    private fun updateAndSaveTodo(newList: List<TodoItem>) {
        // 1. Update Memory (Instant UI update)
        _todoList.value = newList
        // 2. Update Disk (Background save)
        scope.launch { storage?.saveTodoList(newList) }
    }
    fun recordSession(minutes: Int) {
        val newSession = StudySession(durationMinutes = minutes)
        val newList = _history.value + newSession

        _history.value = newList
        scope.launch { storage?.saveHistory(newList) }
    }
}