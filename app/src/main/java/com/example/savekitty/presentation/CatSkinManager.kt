package com.example.savekitty.presentation

import com.example.savekitty.R

object CatSkinManager {

    // Helper to get the correct image resource
    fun getCatImage(skinId: Int, state: CatState): Int {
        return when (skinId) {
            0 -> getOrangeCat(state) // Orange (Default)
            1 -> getBnwCat(state)    // Black & White
            2 -> getGreyCat(state)   // Grey
            3 -> getWhiteCat(state)  // White
            else -> getOrangeCat(state)
        }
    }

    private fun getOrangeCat(state: CatState): Int {
        return when (state) {
            CatState.SLEEP -> R.drawable.cat_sleep
            CatState.HUNGRY -> R.drawable.cat_hungry
            CatState.SIT -> R.drawable.cat_orange
            CatState.KNEAD -> R.drawable.cat_knead_0 // Animation frame start
        }
    }

    // --- FOR THE OTHER CATS ---
    // Since you might not have 'cat_white_sleep.png' yet,
    // I made this logic fallback to the "Sit" pose so at least the COLOR is right.
    // TODO: As you create the images, replace 'R.drawable.cat_white' with 'R.drawable.cat_white_sleep' etc.

    private fun getBnwCat(state: CatState): Int {
        return when (state) {
            CatState.SLEEP -> R.drawable.cat_bnw // Placeholder: Use sitting image until you have sleeping
            CatState.HUNGRY -> R.drawable.cat_bnw // Placeholder
            CatState.SIT -> R.drawable.cat_bnw
            CatState.KNEAD -> R.drawable.cat_bnw
        }
    }

    private fun getGreyCat(state: CatState): Int {
        return when (state) {
            CatState.SLEEP -> R.drawable.cat_grey // Placeholder
            CatState.HUNGRY -> R.drawable.cat_grey // Placeholder
            CatState.SIT -> R.drawable.cat_grey
            CatState.KNEAD -> R.drawable.cat_grey
        }
    }

    private fun getWhiteCat(state: CatState): Int {
        return when (state) {
            CatState.SLEEP -> R.drawable.cat_white // Placeholder
            CatState.HUNGRY -> R.drawable.cat_white // Placeholder
            CatState.SIT -> R.drawable.cat_white
            CatState.KNEAD -> R.drawable.cat_white
        }
    }
}

enum class CatState {
    SIT, SLEEP, HUNGRY, KNEAD
}