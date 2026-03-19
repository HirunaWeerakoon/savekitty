package com.example.savekitty

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.savekitty.data.NotificationHelper
import com.example.savekitty.data.SoundManager
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

        soundManager = SoundManager(applicationContext)
        notificationHelper = NotificationHelper(applicationContext)

        viewModel.setSoundManager(soundManager)
        viewModel.setNotificationHelper(notificationHelper)

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
