package com.ivangames.masmusic

import android.media.MediaPlayer

object PlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    var currentSong: Song? = null
        private set

    var playlist: List<Song> = emptyList()
        private set

    var onSongChanged: ((Song) -> Unit)? = null

    fun setPlaylist(songs: List<Song>) {
        playlist = songs
    }

    fun play(song: Song) {
        stop()
        currentSong = song
        mediaPlayer = MediaPlayer().apply {
            setDataSource(song.path)
            prepare()
            start()
            setOnCompletionListener {
                next()
            }
        }
    }

    fun pause() {
        mediaPlayer?.let { if (it.isPlaying) it.pause() }
    }

    fun resume() {
        mediaPlayer?.let { if (!it.isPlaying) it.start() }
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    fun next() {
        val current = currentSong ?: return
        val index = playlist.indexOf(current)
        if (index >= 0 && index < playlist.size - 1) {
            play(playlist[index + 1])
            onSongChanged?.invoke(playlist[index + 1])
        }
    }

    fun prev() {
        val current = currentSong ?: return
        val index = playlist.indexOf(current)
        if (index > 0) {
            play(playlist[index - 1])
            onSongChanged?.invoke(playlist[index - 1])
        }
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    fun getDuration(): Int = mediaPlayer?.duration ?: 0
}
