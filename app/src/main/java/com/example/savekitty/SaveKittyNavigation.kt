package com.example.savekitty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // All other state collections are safe now
        val navController = rememberNavController()
        val coins by viewModel.biscuits.collectAsState()
        val fish by viewModel.fishCount.collectAsState()
        val timeLeft by viewModel.timeLeft.collectAsState()
        val isTimerRunning by viewModel.isTimerRunning.collectAsState()
        val todoList by viewModel.todoList.collectAsState()
        val isMuted by viewModel.isMutedState.collectAsState()
        val inventory by viewModel.inventory.collectAsState()
        val deceasedCats by viewModel.deceasedCats.collectAsState()
        val history by viewModel.history.collectAsState()
        val catName by viewModel.catName.collectAsState()
        val catSkinId by viewModel.catSkin.collectAsState()
        val health by viewModel.health.collectAsState()
        val isTutorialComplete by viewModel.isTutorialComplete.collectAsState()
        val showHospitalDialog by viewModel.showHospitalDialog.collectAsState()
        val placedItems by viewModel.placedItems.collectAsState()

        val lifecycleOwner = LocalLifecycleOwner.current
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

        NavHost(navController = navController, startDestination = startDest) {
            composable("setup") {
                val deceasedSkins = deceasedCats.map { it.skin }.toSet()

                if (showHospitalDialog) {
                    HospitalDialog(onConfirm = { viewModel.onHospitalDialogDismissed() })
                }

                CatSelectionScreen(
                    deceasedCats = deceasedSkins,
                    onCatSelected = { name, skin ->
                        viewModel.setCatIdentity(name, skin)
                        navController.navigate("room") { popUpTo("setup") { inclusive = true } }
                    }
                )
            }

            composable("room") {
                if (health <= 0 && catName.isNotEmpty()) {
                    LaunchedEffect(Unit) {
                        viewModel.handleGameOver()
                        navController.navigate("setup") { popUpTo("room") { inclusive = true } }
                    }
                }

                RoomScreen(
                    currentHealth = health,
                    coinCount = coins,
                    inventory = inventory,
                    onEat = { food -> viewModel.eatFood(food) },
                    isMuted = isMuted,
                    onToggleMute = { viewModel.toggleMute() },
                    placedItems = placedItems,
                    onDoorClick = { navController.navigate("outside") },
                    onTableClick = { navController.navigate("desk") },
                    onCatClick = { isSleeping -> viewModel.onCatClick(isSleeping) },
                    onStatsClick = { navController.navigate("stats") },
                    onEquipDemo = { type ->
                        val allItems = com.example.savekitty.data.ItemCatalog.decorations.filter { it.type == type }
                        if (allItems.isNotEmpty()) {
                            val randomItem = allItems.random()
                            viewModel.buyDecoration(randomItem)
                            viewModel.equipDecoration(randomItem)
                        }
                    },
                    onWatchAd = { viewModel.earnAdReward() },
                    catSkinId = catSkinId,
                    isTimerRunning = isTimerRunning,
                    showTutorial = !isTutorialComplete,
                    onTutorialFinished = { viewModel.completeTutorial() }
                )
            }

            composable("desk") {
                TimerScreen(
                    timeLeft = timeLeft,
                    isTimerRunning = isTimerRunning,
                    todoList = todoList,
                    currentHealth = health,
                    coinCount = coins,
                    isMuted = isMuted,
                    onToggleMute = { viewModel.toggleMute() },
                    onAddTodo = { text, isDaily -> viewModel.addTodo(text, isDaily) },
                    onToggleTodo = { viewModel.toggleTodo(it) },
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
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("outside") {
                OutsideScreen(
                    onFoodShopClick = { navController.navigate("shop") },
                    onFurnitureShopClick = { navController.navigate("furniture_shop") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("shop") {
                ShopScreen(
                    coinCount = coins,
                    onBuyFood = { food -> viewModel.buyFood(food) },
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
}