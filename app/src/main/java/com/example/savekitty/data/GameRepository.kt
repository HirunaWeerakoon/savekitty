package com.example.savekitty.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.flow.first

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

    private val _inventory = MutableStateFlow<Map<String, Int>>(emptyMap())
    val inventory = _inventory.asStateFlow()

    private val _catName = MutableStateFlow("Kitty")
    val catName = _catName.asStateFlow()

    private val _catSkin = MutableStateFlow(0) // 0 = Orange, 1 = Black, etc.
    val catSkin = _catSkin.asStateFlow()

    // Store IDs of cats that have "passed away" so they can't be picked again
    private val _deceasedCats = MutableStateFlow<Set<Int>>(emptySet())
    val deceasedCats = _deceasedCats.asStateFlow()

    // Track if it's the very first launch for the Tutorial
    private val _isFirstRun = MutableStateFlow(true)
    val isFirstRun = _isFirstRun.asStateFlow()

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
        scope.launch {
            storage?.catNameFlow?.collectLatest { _catName.value = it }
        }
        scope.launch {
            storage?.catSkinFlow?.collectLatest { _catSkin.value = it }
        }
        scope.launch {
            storage?.deceasedCatsFlow?.collectLatest { _deceasedCats.value = it }
        }
        scope.launch {
            storage?.isFirstRunFlow?.collectLatest { _isFirstRun.value = it }
        }
        scope.launch {
            // Wait for data to load
            val lastTime = storage?.lastHealthTimeFlow?.first() ?: 0L
            val currentHealth = _health.value


            val currentTime = System.currentTimeMillis()
            if (lastTime == 0L) {
                storage?.saveLastHealthTime(currentTime)
                return@launch
            }
            val diffMillis = currentTime - lastTime
            if (diffMillis < 0) {
                storage?.saveLastHealthTime(currentTime)
                return@launch
            }

            // Convert to Hours
            val hoursPassed = diffMillis / (1000 * 60 * 60)

            // Rule: 1 HP (Half Heart) per 6 Hours
            val healthToLose = (hoursPassed / 6).toInt()

            if (healthToLose > 0) {
                val currentHealth = storage?.healthFlow?.first() ?: 5
                val newHealth = (currentHealth - healthToLose).coerceAtLeast(0)

                // Save Health
                _health.value = newHealth
                storage?.saveHealth(newHealth)

                // Save Time: Deduct the hours we used, keep the remainder
                val timeAccountedFor = healthToLose * 6 * 60 * 60 * 1000L
                storage?.saveLastHealthTime(lastTime + timeAccountedFor)
            } else {
                // If it's the very first run (or data missing), just set current time
                if (diffMillis < 0) { // Safety check for time travelers (changing settings backwards)
                    storage?.saveLastHealthTime(currentTime)
                }
            }
        }
        scope.launch { storage?.inventoryFlow?.collectLatest { _inventory.value = it } }
        // 2. NEW: CHECK FOR NEW DAY ☀️
        scope.launch {
            // Get the saved date
            val storedList = storage?.todoListFlow?.first() ?: emptyList()
            val lastDate = storage?.lastOpenDateFlow?.first() ?: 0L
            val today = System.currentTimeMillis()

            if (!isSameDay(lastDate, today)) {
                val resetList = storedList.map { item ->
                    if (item.isDaily) {
                        item.copy(isDone = false) // Uncheck it!
                    } else {
                        item // Leave long-term tasks alone
                    }
                }

                // Save the changes
                updateAndSaveTodo(resetList)

                // Save "Today" as the new last open date
                storage?.saveLastOpenDate(today)
            }
        }

    }


    // 2. ACTIONS
    fun setCatIdentity(name: String, skinId: Int) {
        _catName.value = name
        _catSkin.value = skinId
        _isFirstRun.value = false // Tutorial/Setup done

        scope.launch {
            storage?.saveCatIdentity(name, skinId)
            storage?.saveFirstRun(false)
        }
    }

    fun handleGameOver() {
        // 1. Mark current cat as deceased
        val currentSkin = _catSkin.value
        val newDeceasedSet = _deceasedCats.value + currentSkin

        _deceasedCats.value = newDeceasedSet
        scope.launch { storage?.saveDeceasedCats(newDeceasedSet) }

        // 2. Reset Game State (Punishment)
        _health.value = 5
        _biscuits.value = 0 // Lost all money
        _fishCount.value = 0
        _inventory.value = emptyMap() // Lost items

        scope.launch {
            storage?.saveHealth(5)
            storage?.saveBiscuits(0)
            storage?.saveFish(0)
            storage?.saveInventory(emptyMap())
        }
    }
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
    fun addTodo(text: String,isDaily: Boolean) {
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
    // Helper to check if two timestamps are on the same calendar day
    private fun isSameDay(t1: Long, t2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    fun buyFood(food: Food) {
        if (spendBiscuits(food.price)) {
            val currentCount = _inventory.value[food.id] ?: 0
            val newInventory = _inventory.value.toMutableMap()
            newInventory[food.id] = currentCount + 1

            // Save
            _inventory.value = newInventory
            scope.launch { storage?.saveInventory(newInventory) }
        }
    }

    fun eatFood(food: Food) {
        val currentCount = _inventory.value[food.id] ?: 0

        // Check if we have food AND health isn't full
        if (currentCount > 0 && _health.value < 10) {
            // 1. Remove 1 item
            val newInventory = _inventory.value.toMutableMap()
            newInventory[food.id] = currentCount - 1

            // 2. Add Health (Don't go over 10)
            val newHealth = (_health.value + food.healthPoints).coerceAtMost(10)

            // 3. Save Both
            _inventory.value = newInventory
            _health.value = newHealth

            scope.launch {
                storage?.saveInventory(newInventory)
                storage?.saveHealth(newHealth)
            }
        }
    }
}