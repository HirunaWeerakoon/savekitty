package com.example.savekitty

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.savekitty.data.GameRepository.catName
import com.example.savekitty.presentation.CatSelectionScreen
import com.example.savekitty.presentation.HospitalDialog
import com.example.savekitty.presentation.OutsideScreen
import com.example.savekitty.presentation.shop.ShopScreen
import com.example.savekitty.presentation.timer.LaptopScreen
import com.example.savekitty.presentation.timer.TimerScreen
import com.example.savekitty.ui.RoomScreen
import com.example.savekitty.viewModel.GameViewModel
import com.example.savekitty.presentation.StatsScreen
import com.example.savekitty.presentation.shop.FurnitureShopScreen


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
    val inventory by viewModel.inventory.collectAsState()
    val isFirstRun by viewModel.isFirstRun.collectAsState()
    val deceasedCats by viewModel.deceasedCats.collectAsState()
    val history by viewModel.history.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val catName by viewModel.catName.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.onAppPause()
                Lifecycle.Event.ON_RESUME -> viewModel.onAppResume()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val startDest = if (catName.isEmpty()) "setup" else "room"
    val placedItems by viewModel.placedItems.collectAsState()

    // 2. The Navigation Graph
    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        composable("setup") {
            // 1. Collect the list from the ViewModel
            val deceasedCats by viewModel.deceasedCats.collectAsState()
            val health by viewModel.health.collectAsState()
            val isFirstRun by viewModel.isFirstRun.collectAsState()

            // Keep your hospital logic if needed
            if (health == 0 && !isFirstRun) {
                HospitalDialog(
                    onConfirm = { viewModel.handleGameOver() }
                )
            }

            CatSelectionScreen(
                deceasedCats = deceasedCats, // <--- ADD THIS LINE
                onCatSelected = { name, skin ->
                    viewModel.setCatIdentity(name, skin)
                    navController.navigate("room") {
                        popUpTo("setup") { inclusive = true }
                    }
                }
            )
        }
        // 🏠 SCREEN 1: THE ROOM
        composable("room") {
            val catSkinId by viewModel.catSkin.collectAsState()
            RoomScreen(
                currentHealth = health,
                coinCount = coins,
                fishCount = fish,
                inventory = inventory,
                onEat = { food -> viewModel.eatFood(food) },
                isMuted = isMuted,
                onToggleMute = { viewModel.toggleMute() },
                placedItems = placedItems,

                // 🔊 PLAY SOUNDS ON CLICK
                onDoorClick = {
                    viewModel.playDoorSound() // <--- FIX: Play Sound!
                    navController.navigate("outside")
                },
                onTableClick = {
                    viewModel.playClickSound() // <--- FIX: Play Sound!
                    navController.navigate("desk")
                },
                onBuyFish = { viewModel.buyFish() },
                onFeedCat = { viewModel.consumeFish() },
                onCatClick = { isSleeping ->
                    viewModel.onCatClick(isSleeping)
                },
                onStatsClick = { navController.navigate("stats") },
                onEquipDemo = { type ->
                    // Logic: Find the next item of this type and equip it
                    // This is just for testing!
                    val allItems =
                        com.example.savekitty.data.ItemCatalog.decorations.filter { it.type == type }
                    if (allItems.isNotEmpty()) {
                        // Pick random or cycle
                        val randomItem = allItems.random()
                        viewModel.buyDecoration(randomItem)
                        viewModel.equipDecoration(randomItem)
                    }
                } ,
                onWatchAd = { viewModel.earnAdReward() },
                catSkinId = catSkinId,
                isFirstRun = isFirstRun,
                onTutorialFinished = { viewModel.completeTutorial() },
                isTimerRunning = isTimerRunning


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
                onWatchAd = { viewModel.earnAdReward() }

            )
        }
        composable("laptop_zoom") {
            LaptopScreen(
                timeLeft = timeLeft,
                isTimerRunning = isTimerRunning,
                onToggleTimer = { viewModel.toggleTimer() },
                onSetTime = { minutes -> viewModel.setTime(minutes) },
                onBackClick = { navController.popBackStack() } // Back to Desk,


            )
        }
        composable("outside") {
            OutsideScreen(
                onFoodShopClick = {
                    navController.navigate("shop")
                },
                onFurnitureShopClick = {
                    navController.navigate("furniture_shop")
                },
                onBackClick = { navController.popBackStack() }
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
        composable("furniture_shop") {
            FurnitureShopScreen(
                coinCount = coins,
                inventory = inventory,
                placedItems = placedItems,
                onBuy = { item -> viewModel.buyDecoration(item) },
                onEquip = { item -> viewModel.equipDecoration(item) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("stats") {

            StatsScreen(
                history = history,
                onBackClick = { navController.popBackStack() }
            )
        }



    }
}