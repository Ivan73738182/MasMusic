package com.ivangames.masmusic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var infoText: TextView
    private lateinit var songsList: RecyclerView
    private lateinit var miniPlayer: LinearLayout
    private lateinit var currentSong: TextView
    private lateinit var playPauseBtn: Button
    private lateinit var prevBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var miniProgress: ProgressBar

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgress = object : Runnable {
        override fun run() {
            val dur = PlayerManager.getDuration()
            if (dur > 0) {
                val pos = PlayerManager.getCurrentPosition()
                miniProgress.progress = (pos * 100 / dur)
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) loadMusic()
        else Toast.makeText(this, "Без разрешения музыку не найти 😢", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        infoText = findViewById(R.id.infoText)
        songsList = findViewById(R.id.songsList)
        songsList.layoutManager = LinearLayoutManager(this)
        miniPlayer = findViewById(R.id.miniPlayer)
        currentSong = findViewById(R.id.currentSong)
        playPauseBtn = findViewById(R.id.playPauseBtn)
        prevBtn = findViewById(R.id.prevBtn)
        nextBtn = findViewById(R.id.nextBtn)
        miniProgress = findViewById(R.id.miniProgress)

        playPauseBtn.setOnClickListener {
            if (PlayerManager.isPlaying()) {
                PlayerManager.pause()
                playPauseBtn.text = "▶"
            } else {
                PlayerManager.resume()
                playPauseBtn.text = "⏸"
            }
        }

        nextBtn.setOnClickListener { PlayerManager.next() }
        prevBtn.setOnClickListener { PlayerManager.prev() }

        PlayerManager.onSongChanged = { song ->
            currentSong.text = song.title
            playPauseBtn.text = "⏸"
        }

        handler.post(updateProgress)
        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadMusic()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun loadMusic() {
        val songs = MusicScanner.scanMusic(this)
        infoText.text = "Найдено песен: ${songs.size}"
        PlayerManager.setPlaylist(songs)

val adapter = SongAdapter(
    songs,
    onClick = { song ->
        try {
            PlayerManager.play(song)
            currentSong.text = song.title
            playPauseBtn.text = "⏸"
            miniPlayer.visibility = View.VISIBLE
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
        }
    },
onMenu = { song, view ->
    val popup = android.widget.PopupMenu(this, view)
    val favText = if (PlayerManager.isFavorite(song)) "💔 Убрать из избранного"
                  else "❤️ В избранное"
    popup.menu.add(favText)
    popup.menu.add("🗑 Удалить из списка")
    popup.setOnMenuItemClickListener { item ->
        when (item.title.toString()) {
            "❤️ В избранное" -> {
                PlayerManager.toggleFavorite(song)
                Toast.makeText(this, "❤️ Добавлено: ${song.title}", Toast.LENGTH_SHORT).show()
                true
            }
            "💔 Убрать из избранного" -> {
                PlayerManager.toggleFavorite(song)
                Toast.makeText(this, "💔 Убрано: ${song.title}", Toast.LENGTH_SHORT).show()
                true
            }
            "🗑 Удалить из списка" -> {
                Toast.makeText(this, "Удалено: ${song.title}", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }
    popup.show()
}
)
songsList.adapter = adapter
}

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgress)
    }
}
