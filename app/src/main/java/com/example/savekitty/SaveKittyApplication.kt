package com.example.savekitty

import android.app.Application

class SaveKittyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization logic is now handled by the GameViewModel
    }
}
