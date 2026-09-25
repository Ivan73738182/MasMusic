package com.ivangames.masmusic

import android.media.MediaPlayer

object PlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    fun play(path: String) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            start()
        }
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
}
