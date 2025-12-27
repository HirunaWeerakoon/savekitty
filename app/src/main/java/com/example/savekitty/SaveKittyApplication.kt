package com.example.savekitty

import android.app.Application
import com.example.savekitty.data.GameRepository

class SaveKittyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Wake up the Repository and load saved data
        GameRepository.initialize(this)
    }
}