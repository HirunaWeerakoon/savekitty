package com.example.savekitty.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.map

// Create the DataStore file (like a mini database)
private val Context.dataStore by preferencesDataStore(name = "savekitty_data")

class GameStorage(private val context: Context) {

    private val gson = Gson()
    // Define the Keys
    companion object {
        val KEY_COINS = intPreferencesKey("coins")
        val KEY_HEALTH = intPreferencesKey("health")
        val KEY_FISH = intPreferencesKey("fish")
        val KEY_TODO_LIST = stringPreferencesKey("todo_list")
    }

    // --- READ DATA (Flows) ---
    // If no data exists, return default values (100 coins, 5 health)
    val coinsFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[KEY_COINS] ?: 100 }

    val healthFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[KEY_HEALTH] ?: 5 }

    val fishFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[KEY_FISH] ?: 0 }

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

    // --- WRITE TODO LIST ---
    suspend fun saveTodoList(list: List<TodoItem>) {
        val json = gson.toJson(list) // Convert List to JSON String
        context.dataStore.edit { it[KEY_TODO_LIST] = json }
    }

    // --- WRITE DATA (Suspend Functions) ---
    suspend fun saveCoins(amount: Int) {
        context.dataStore.edit { it[KEY_COINS] = amount }
    }

    suspend fun saveHealth(amount: Int) {
        context.dataStore.edit { it[KEY_HEALTH] = amount }
    }

    suspend fun saveFish(amount: Int) {
        context.dataStore.edit { it[KEY_FISH] = amount }
    }
}