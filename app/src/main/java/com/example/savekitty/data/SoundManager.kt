package com.example.savekitty.data

import android.content.Context
import android.media.MediaPlayer
import com.example.savekitty.R

class SoundManager(private val context: Context) {

    fun playMeow() {
        playSound(R.raw.sfx_meow) // Make sure file name matches exactly!
    }

    fun playChing() {
        playSound(R.raw.sfx_ching)
    }

    fun playLevelUp() {
        playSound(R.raw.sfx_level_up)
    }

    private fun playSound(resId: Int) {
        // Create and play a one-shot sound
        val mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release() // Clean up memory when done
        }
        mediaPlayer.start()
    }
}