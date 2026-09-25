package com.ivangames.masmusic

import android.content.Context
import android.provider.MediaStore

object MusicScanner {

    fun scanMusic(context: Context): List<Song> {
        val songs = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )
        cursor?.use {
            val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val pathCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val title = it.getString(titleCol) ?: "Без названия"
                val artist = it.getString(artistCol) ?: "Неизвестный"
                val path = it.getString(pathCol) ?: ""
                val duration = it.getLong(durationCol)
                if (path.isNotEmpty()) {
                    songs.add(Song(title, artist, path, duration))
                }
            }
        }
        return songs
    }
}
