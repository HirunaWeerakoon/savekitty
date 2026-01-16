package com.example.savekitty.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.map
import com.example.savekitty.data.DecorationType

// Create the DataStore file (like a mini database)
private val Context.dataStore by preferencesDataStore(name = "savekitty_data")

class GameStorage(private val context: Context) {

    private val gson = Gson()
    // Define the Keys
    companion object {
        val KEY_BISCUITS = intPreferencesKey("biscuits")
        val KEY_HEALTH = intPreferencesKey("health")
        val KEY_FISH = intPreferencesKey("fish")
        val KEY_TODO_LIST = stringPreferencesKey("todo_list")
        val KEY_HISTORY = stringPreferencesKey("study_history")

        val KEY_INVENTORY = stringPreferencesKey("food_inventory")
        val KEY_LAST_HEALTH_TIME = longPreferencesKey("last_health_time")
        val KEY_CAT_NAME = stringPreferencesKey("cat_name")
        val KEY_CAT_SKIN = intPreferencesKey("cat_skin")
        val KEY_DECEASED_CATS = stringSetPreferencesKey("deceased_cats") // Stores IDs of dead cats
        val KEY_IS_FIRST_RUN = booleanPreferencesKey("is_first_run")
        val KEY_LAST_OPEN_DATE = longPreferencesKey("last_open_date")
        val KEY_PLACED_ITEMS = stringPreferencesKey("placed_items")
        private val TIMER_END_TIME_KEY = longPreferencesKey("timer_end_time")
        private val IS_FIRST_RUN_KEY = booleanPreferencesKey("is_first_run")

    }

    // --- READ DATA (Flows) ---
    // If no data exists, return default values (100 coins, 5 health)
    val biscuitsFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[KEY_BISCUITS] ?: 100 }

    val healthFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[KEY_HEALTH] ?: 5 }

    val fishFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[KEY_FISH] ?: 0 }

    val catNameFlow: Flow<String> = context.dataStore.data.map { it[KEY_CAT_NAME] ?: "" }
    val catSkinFlow: Flow<Int> = context.dataStore.data.map { it[KEY_CAT_SKIN] ?: 0 }
    val deceasedCatsFlow: Flow<Set<Int>> = context.dataStore.data.map {
        it[KEY_DECEASED_CATS]?.map { idStr -> idStr.toInt() }?.toSet() ?: emptySet()
    }


    val todoListFlow: Flow<List<TodoItem>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[KEY_TODO_LIST] ?: ""
            if (json.isEmpty()) {
                emptyList()
            } else {
                // Convert JSON String back to List<TodoItem>
                val type = object : TypeToken<List<TodoItem>>() {}.type
                gson.fromJson(json, type)
            }
        }
    val historyFlow: Flow<List<StudySession>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[KEY_HISTORY] ?: ""
            if (json.isEmpty()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<StudySession>>() {}.type
                gson.fromJson(json, type)
            }
        }
    val lastOpenDateFlow: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_LAST_OPEN_DATE] ?: 0L
        }
    // --- READ INVENTORY (Map of ID -> Count) ---
    val inventoryFlow: Flow<Map<String, Int>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[KEY_INVENTORY] ?: ""
            if (json.isEmpty()) {
                // Default: 0 of everything
                emptyMap()
            } else {
                val type = object : TypeToken<Map<String, Int>>() {}.type
                gson.fromJson(json, type)
            }
        }
    val placedItemsFlow: Flow<Map<DecorationType, String>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[KEY_PLACED_ITEMS] ?: ""
            if (json.isEmpty()) {
                emptyMap()
            } else {
                // Use Gson to parse the JSON back into a Map
                val typeToken = object : TypeToken<Map<DecorationType, String>>() {}.type
                gson.fromJson(json, typeToken)
            }
        }
    val timerEndTimeFlow: Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[TIMER_END_TIME_KEY] ?: 0L }
    val isFirstRunFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[IS_FIRST_RUN_KEY] ?: true }
    // SAVE DATE
    suspend fun saveInventory(inventory: Map<String, Int>) {
        val json = gson.toJson(inventory)
        context.dataStore.edit { it[KEY_INVENTORY] = json }
    }
    suspend fun saveLastOpenDate(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LAST_OPEN_DATE] = timestamp
        }
    }
    val lastHealthTimeFlow: Flow<Long> = context.dataStore.data
        .map { it[KEY_LAST_HEALTH_TIME] ?: 0L
        }

    suspend fun saveLastHealthTime(timestamp: Long) {
        context.dataStore.edit { it[KEY_LAST_HEALTH_TIME] = timestamp }
    }
    suspend fun saveCatIdentity(name: String, skin: Int) {
        context.dataStore.edit {
            it[KEY_CAT_NAME] = name
            it[KEY_CAT_SKIN] = skin
        }
    }

    suspend fun saveDeceasedCats(ids: Set<Int>) {
        context.dataStore.edit {
            // DataStore only supports Set<String>, so we convert Int -> String
            it[KEY_DECEASED_CATS] = ids.map { id -> id.toString() }.toSet()
        }
    }

    suspend fun saveFirstRun(isFirst: Boolean) {
        context.dataStore.edit { it[KEY_IS_FIRST_RUN] = isFirst }
    }






    // --- WRITE TODO LIST ---
    suspend fun saveTodoList(list: List<TodoItem>) {
        val json = gson.toJson(list) // Convert List to JSON String
        context.dataStore.edit { it[KEY_TODO_LIST] = json }
    }

    suspend fun savePlacedItems(items: Map<DecorationType, String>) {
        val json = gson.toJson(items)
        context.dataStore.edit { it[KEY_PLACED_ITEMS] = json }
    }
    // --- WRITE DATA (Suspend Functions) ---
    suspend fun saveBiscuits(amount: Int) {
        context.dataStore.edit { it[KEY_BISCUITS] = amount }
    }

    suspend fun saveHealth(amount: Int) {
        context.dataStore.edit { it[KEY_HEALTH] = amount }
    }

    suspend fun saveFish(amount: Int) {
        context.dataStore.edit { it[KEY_FISH] = amount }
    }
    suspend fun saveHistory(list: List<StudySession>) {
        val json = gson.toJson(list)
        context.dataStore.edit { it[KEY_HISTORY] = json }
    }
    suspend fun saveTimerEndTime(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[TIMER_END_TIME_KEY] = timestamp
        }
    }
    suspend fun saveIsFirstRun(isFirstRun: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_RUN_KEY] = isFirstRun
        }
    }

}