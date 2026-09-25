package com.ivangames.masmusic

import android.content.ContentUris
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(
    private val songs: List<Song>,
    private val onClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.songCover)
        val title: TextView = view.findViewById(R.id.songTitle)
        val artist: TextView = view.findViewById(R.id.songArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.title.text = song.title
        holder.artist.text = song.artist

        // Обложка из MediaStore
        val albumUri = ContentUris.withAppendedId(
            android.net.Uri.parse("content://media/external/audio/albumart"),
            song.albumId
        )
        holder.cover.setImageURI(albumUri)
        if (holder.cover.drawable == null) {
            holder.cover.setImageResource(android.R.drawable.ic_media_play)
        }

        holder.itemView.setOnClickListener { onClick(song) }
    }

    override fun getItemCount(): Int = songs.size
}
