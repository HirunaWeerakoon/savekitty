package com.example.savekitty.data

import android.content.Context
import android.media.MediaPlayer
import com.example.savekitty.R

class SoundManager(private val context: Context) {

    private var musicPlayer: MediaPlayer? = null
    var isMuted = false

    // --- SFX (One-shot sounds) ---
    fun playSFX(resId: Int) {
        if (isMuted) return
        try {
            val mp = MediaPlayer.create(context, resId)
            mp.setOnCompletionListener { it.release() }
            mp.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun playMeow() {
        playSound(R.raw.sfx_meow) // Make sure file name matches exactly!
    }

    fun playChing() {
        playSound(R.raw.sfx_ching)
    }

    fun playLevelUp() {
        playSound(R.raw.sfx_level_up)
    }
    fun playDoorOpen() = playSFX(R.raw.sfx_door)
    fun playNotebookFlip() = playSFX(R.raw.sfx_page_flip)
    fun playButtonPress() = playSFX(R.raw.sfx_click)

    private fun playSound(resId: Int) {
        // Create and play a one-shot sound
        val mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release() // Clean up memory when done
        }
        mediaPlayer.start()
    }
    fun playMusic(resId: Int) {
        if (isMuted) return

        // Don't restart if already playing the same song
        if (musicPlayer != null && musicPlayer!!.isPlaying) return

        stopMusic() // Stop previous song if any

        try {
            musicPlayer = MediaPlayer.create(context, resId)
            musicPlayer?.isLooping = true // Loop forever
            musicPlayer?.setVolume(1f, 1f) // 50% volume so it's not too loud
            musicPlayer?.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun stopMusic() {
        musicPlayer?.stop()
        musicPlayer?.release()
        musicPlayer = null
    }

    // --- TOGGLES ---
    fun toggleMute() {
        isMuted = !isMuted
        if (isMuted) {
            stopMusic()
        } else {
            // Resume theme music if unmuted (Optional: pass current screen logic here)
            playMusic(R.raw.music_lofi_beat)
        }
    }
    fun pauseMusic() {
        if (musicPlayer?.isPlaying == true) {
            musicPlayer?.pause()
        }
    }

    fun resumeMusic() {
        // Only resume if we are NOT muted and the player exists
        if (!isMuted && musicPlayer != null) {
            musicPlayer?.start()
        }
    }
    fun playPurr() {
        playSFX(R.raw.sfx_purr)
    }

    fun playEat() {
        playSFX(R.raw.sfx_eat)
    }
}