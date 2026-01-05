package com.example.savekitty.data

import com.example.savekitty.R

data class Food(
    val id: String,
    val name: String,
    val price: Int,
    val healthPoints: Int, // 1 point = Half Heart, 2 points = Full Heart
    val imageRes: Int
)

// THE MENU 🍽️
val FoodMenu = listOf(
    Food("fish", "Fish", 5, 1, R.drawable.ic_fish),         // 0.5 Hearts
    Food("milk", "Milk", 15, 2, R.drawable.ic_food_milk),       // 1 Heart
    Food("sushi", "Sushi", 30, 4, R.drawable.ic_food_sushi),     // 2 Hearts
    Food("chicken", "Chicken", 50, 6, R.drawable.ic_food_chicken), // 3 Hearts
    Food("steak", "Steak", 80, 8, R.drawable.ic_food_steak),     // 4 Hearts
    Food("caviar", "Caviar", 150, 10, R.drawable.ic_food_caviar)   // 5 Hearts (Full Heal)
)