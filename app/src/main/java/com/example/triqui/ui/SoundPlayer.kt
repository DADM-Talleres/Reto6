// ui/SoundPlayer.kt
package com.example.triqui.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.triqui.R
import com.example.triqui.logic.Player

class SoundPlayer(context: Context) {
    private val soundPool: SoundPool
    private var moveSoundId: Int = 0
    private var isLoaded = false

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(attributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, _, status ->
            isLoaded = status == 0
        }

        moveSoundId = soundPool.load(context, R.raw.realtap, 1)
    }

    fun playMove(player: Player) {
        if (!isLoaded) return
        val pitch = if (player == Player.X) 1f else 0.7f
        soundPool.play(moveSoundId, 1f, 1f, 0, 0, pitch)
    }

    fun release() {
        soundPool.release()
    }
}