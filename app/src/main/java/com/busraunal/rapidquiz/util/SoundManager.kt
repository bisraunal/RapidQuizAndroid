package com.busraunal.rapidquiz.util

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class SoundManager(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null
    var isMuted: Boolean = false
        private set

    fun playMusic(url: String?) {
        if (url.isNullOrBlank()) return

        stopMusic()

        try {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                repeatMode = Player.REPEAT_MODE_ALL
                volume = if (isMuted) 0f else 0.4f
                prepare()
                play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        exoPlayer?.volume = if (isMuted) 0f else 0.4f
        return isMuted
    }

    fun stopMusic() {
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
    }
}
