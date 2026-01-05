package com.example.savekitty

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.savekitty.data.GameRepository.inventory
import com.example.savekitty.presentation.shop.ShopScreen
import com.example.savekitty.presentation.timer.LaptopScreen
import com.example.savekitty.presentation.timer.TimerScreen
import com.example.savekitty.ui.RoomScreen
import com.example.savekitty.viewModel.GameViewModel
import com.example.savekitty.presentation.StatsScreen

@Composable
fun SaveKittyNavigation(viewModel: GameViewModel) {
    val navController = rememberNavController()

    // 1. Collect State from ViewModel (One place to rule them all)
    val health by viewModel.health.collectAsState()
    val coins by viewModel.biscuits.collectAsState()
    val fish by viewModel.fishCount.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val todoList by viewModel.todoList.collectAsState()
    val isMuted by viewModel.isMutedState.collectAsState()

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
                inventory = inventory,
                onEat = { food -> viewModel.eatFood(food) },
                isMuted = isMuted,
                onToggleMute = { viewModel.toggleMute() },

                // 🔊 PLAY SOUNDS ON CLICK
                onDoorClick = {
                    viewModel.playDoorSound() // <--- FIX: Play Sound!
                    navController.navigate("shop")
                },
                onTableClick = {
                    viewModel.playClickSound() // <--- FIX: Play Sound!
                    navController.navigate("desk")
                },
                onBuyFish = { viewModel.buyFish() },
                onFeedCat = { viewModel.consumeFish() },
                onCatClick = { viewModel.onCatClick() },


            )
        }
        composable("desk") {
            TimerScreen(
                timeLeft = timeLeft,
                isTimerRunning = isTimerRunning,
                todoList = todoList, // <--- PASS DATA
                currentHealth = health, // <--- NEW
                coinCount = coins,      // <--- NEW

                isMuted = isMuted,
                onToggleMute = { viewModel.toggleMute() },

                // 🔊 ACTIONS
                onAddTodo = { text, isDaily ->
                    viewModel.addTodo(text, isDaily) },
                onToggleTodo = {
                    viewModel.playClickSound()
                    viewModel.toggleTodo(it)
                },
                onDeleteTodo = { viewModel.deleteTodo(it) },
                onLaptopClick = { navController.navigate("laptop_zoom") },
                onBackClick = { navController.popBackStack() },
                onBooksClick = { navController.navigate("stats") },
                onPlayPageTurn = { viewModel.playNotebookSound() },

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
        composable("shop") {
            ShopScreen(
                coinCount = coins, // Use the variable you collected at the top
                onBuyFood = { food ->
                    viewModel.buyFood(food) // <--- Connect the wire!
                },
                onBackClick = { navController.popBackStack() },
            )
        }

        composable("stats") {
            val history by viewModel.history.collectAsState()

            StatsScreen(
                history = history,
                onBackClick = { navController.popBackStack() }
            )
        }



    }
}