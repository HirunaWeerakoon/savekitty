package com.example.savekitty

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.savekitty.data.GameRepository
import com.example.savekitty.data.NotificationHelper
import com.example.savekitty.data.SoundManager
import com.example.savekitty.ui.RoomScreen
import com.example.savekitty.ui.theme.SaveKittyTheme
import com.example.savekitty.viewModel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<GameViewModel>()

    private lateinit var soundManager: SoundManager
    private lateinit var notificationHelper: NotificationHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result logic (Optional: Show toast if denied)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = GameRepository
        repository.initialize(this)
        soundManager = SoundManager(this)
        notificationHelper = NotificationHelper(this)
        val viewModel = GameViewModel()
        viewModel.setSoundManager(soundManager)
        viewModel.setNotificationHelper(notificationHelper)
        viewModel.setContext(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        enableEdgeToEdge()
        setContent {
            SaveKittyTheme {
                SaveKittyNavigation(viewModel = viewModel)
            }
        }

    }
    override fun onStop() {
        super.onStop()
        viewModel.onAppBackgrounded()
    }
    override fun onStart() {
        super.onStart()
        viewModel.onAppResume()
    }
}

