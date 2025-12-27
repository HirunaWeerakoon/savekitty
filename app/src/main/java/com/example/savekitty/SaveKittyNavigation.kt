package com.example.savekitty

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.savekitty.presentation.timer.LaptopScreen
import com.example.savekitty.presentation.timer.TimerScreen
import com.example.savekitty.ui.RoomScreen
import com.example.savekitty.viewModel.GameViewModel

@Composable
fun SaveKittyNavigation(viewModel: GameViewModel) {
    val navController = rememberNavController()

    // 1. Collect State from ViewModel (One place to rule them all)
    val health by viewModel.health.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val fish by viewModel.fishCount.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()

    // 2. The Navigation Graph
    NavHost(
        navController = navController,
        startDestination = "room"
    ) {
        // 🏠 SCREEN 1: THE ROOM
        composable("room") {
            RoomScreen(
                currentHealth = health,
                coinCount = coins,
                fishCount = fish,
                onBuyFish = { viewModel.buyFish() },
                onFeedCat = { viewModel.consumeFish() },
                onTableClick = {
                    // Navigate to Timer
                    navController.navigate("desk")
                },
                onDoorClick = { /* Will navigate to Shop later */ },
                onCatClick = { viewModel.onCatClick() }
            )
        }
        composable("desk") {
            TimerScreen(
                timeLeft = timeLeft,
                isTimerRunning = isTimerRunning,
                onLaptopClick = { navController.navigate("laptop_zoom") }, // Zoom In
                onBackClick = { navController.popBackStack() } // Back to Room
            )
        }
        composable("laptop_zoom") {
            LaptopScreen(
                timeLeft = timeLeft,
                isTimerRunning = isTimerRunning,
                onToggleTimer = { viewModel.toggleTimer() },
                onSetTime = { minutes -> viewModel.setTimer(minutes) },
                onBackClick = { navController.popBackStack() } // Back to Desk
            )
        }



    }
}