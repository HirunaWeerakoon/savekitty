package com.example.savekitty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savekitty.ui.RoomScreen
import com.example.savekitty.ui.theme.SaveKittyTheme
import com.example.savekitty.viewModel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaveKittyTheme {
                // 1. Listen to the ViewModel state
                // "collectAsState" automatically updates the UI when values change
                val health by viewModel.health.collectAsState()
                val coins by viewModel.coins.collectAsState()
                val fish by viewModel.fishCount.collectAsState()

                // 2. Pass State & Actions to the Screen
                RoomScreen(
                    currentHealth = health,
                    coinCount = coins,
                    fishCount = fish, // <--- Pass it
                    onBuyFish = { viewModel.buyFish() }, // <--- Connect Action
                    onFeedCat = { viewModel.consumeFish() }, // <--- Connect Action
                    onTableClick = { /* Navigate Timer */ },
                    onDoorClick = { /* Navigate Shop */ },
                    onCatClick = { viewModel.onCatClick() }
                )
            }
        }

    }
}

