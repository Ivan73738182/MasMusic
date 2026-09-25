package com.ivangames.masmusic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadMusic()
        } else {
            Toast.makeText(this, "Без разрешения музыку не найти 😢", Toast.LENGTH_LONG).show()
        }
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

        playPauseBtn.setOnClickListener {
            if (PlayerManager.isPlaying()) {
                PlayerManager.pause()
                playPauseBtn.text = "▶"
            } else {
                PlayerManager.resume()
                playPauseBtn.text = "⏸"
            }
        }

        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission)
            == PackageManager.PERMISSION_GRANTED) {
            loadMusic()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun loadMusic() {
        val songs = MusicScanner.scanMusic(this)
        infoText.text = "Найдено песен: ${songs.size}"

        val adapter = SongAdapter(songs) { song ->
            try {
                PlayerManager.play(song.path)
                currentSong.text = song.title
                playPauseBtn.text = "⏸"
                miniPlayer.visibility = View.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(this, "Не удалось воспроизвести: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        songsList.adapter = adapter
    }
}
