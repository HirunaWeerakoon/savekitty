package com.example.savekitty.data

import com.example.savekitty.R

// 1. THE SLOTS (Where things can go)
enum class DecorationType {
    CLOCK,      // Fixed position: Above fireplace
    RUG,        // Fixed position: Center floor
    PLANT,      // Fixed position: Left corner
    POSTER      // Fixed position: Right wall
}

// 2. THE ITEM MODEL
data class Decoration(
    val id: String,
    val name: String,
    val price: Int,
    val imageRes: Int,
    val type: DecorationType
)

// 3. THE CATALOG (Your Shop Database)
object ItemCatalog {
    val decorations = listOf(
        // --- CLOCKS (Slot: Above Fireplace) ---
        Decoration("clock_analog", "Classic Clock", 50, R.drawable.prop_clock_analog, DecorationType.CLOCK),
        Decoration("clock_digital", "Digital Clock", 150, R.drawable.prop_clock_digital, DecorationType.CLOCK),
        //Decoration("clock_cat", "Kitty Clock", 300, R.drawable.prop_clock_cat, DecorationType.CLOCK),

        // --- RUGS (Slot: Floor) ---
        //Decoration("rug_red", "Persian Rug", 100, R.drawable.prop_rug_red, DecorationType.RUG),
       // Decoration("rug_blue", "Cozy Mat", 80, R.drawable.prop_rug_blue, DecorationType.RUG)
    )

    // Helper to find an item by ID
    fun getById(id: String): Decoration? = decorations.find { it.id == id }
}