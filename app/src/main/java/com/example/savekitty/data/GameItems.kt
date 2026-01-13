package com.example.savekitty.data

import com.example.savekitty.R

// 1. THE SLOTS (Where things can go)
enum class DecorationType {
    CLOCK,      // Fixed position: Above fireplace
         // Fixed position: Left corner
        // Fixed position: Right wall
    TOP_SHELF  ,    // Fixed position: Right wall
    SMALL_SHELF,
    BIG_SHELF,
    SOFA,
    RUG,        // Fixed position: Center floor
    PLANT,
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
        Decoration("clock_analog", "Classic Clock", 10, R.drawable.prop_clock_analog, DecorationType.CLOCK),
        Decoration("clock_digital", "Digital Clock", 10, R.drawable.prop_clock_digital, DecorationType.CLOCK),
        Decoration("clock_cat", "Kitty Clock", 10, R.drawable.prop_clock_kitty, DecorationType.CLOCK),
        Decoration("clock_girls", "Girls Clock", 10, R.drawable.prop_clock_girls, DecorationType.CLOCK),
        Decoration("clock_boys", "Boys Clock", 10, R.drawable.prop_clock_space, DecorationType.CLOCK),
        Decoration("clock_old", "Old Clock", 10, R.drawable.prop_clock_movies, DecorationType.CLOCK),

        // --- RUGS (Slot: Floor) ---
        //Decoration("rug_red", "Persian Rug", 100, R.drawable.prop_rug_red, DecorationType.RUG),
       // Decoration("rug_blue", "Cozy Mat", 80, R.drawable.prop_rug_blue, DecorationType.RUG)
        Decoration("top_shelf_1","top shelf 1",10,R.drawable.top_self_1,DecorationType.TOP_SHELF),
        Decoration("top_shelf_2","top shelf 2",10,R.drawable.top_self_2,DecorationType.TOP_SHELF),
        Decoration("top_shelf_3","top shelf 3",10,R.drawable.top_self_3,DecorationType.TOP_SHELF),

        Decoration("small_shelf_2","small shelf 2",10,R.drawable.small_shelf_2,DecorationType.SMALL_SHELF),
        Decoration("small_shelf_3","small shelf 3",10,R.drawable.small_shelf_3,DecorationType.SMALL_SHELF),
        Decoration("small_shelf_4","small shelf 4",10,R.drawable.small_shelf_4,DecorationType.SMALL_SHELF),

        Decoration("big_shelf_1","big shelf 1",10,R.drawable.big_shelf_1,DecorationType.BIG_SHELF),
        Decoration("big_shelf_2","big shelf 2",10,R.drawable.big_shelf_2,DecorationType.BIG_SHELF),
        Decoration("big_shelf_3","big shelf 3",10,R.drawable.big_shelf_3,DecorationType.BIG_SHELF),

        Decoration("sofa_1","sofa 1",10,R.drawable.sofa_pink,DecorationType.SOFA),


    )

    // Helper to find an item by ID
    fun getById(id: String): Decoration? = decorations.find { it.id == id }
}