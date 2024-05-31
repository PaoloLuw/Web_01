package com.luwliette.ztmelody_02

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luwliette.ztmelody_02.database.SongDatabase

class AlbumActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_album)

        val artistName = intent.getStringExtra("ARTIST_NAME") ?: return

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        val songDatabase = SongDatabase(this)
        val songList = songDatabase.getSongsByArtist(artistName)

        val countryList = ArrayList<CountryModelActivity>()
        val songPaths = songList.map { it.data }

        songList.forEach { song ->
            countryList.add(CountryModelActivity(song.title, R.drawable.ic_music_list))
        }

        recyclerView.adapter = CountryAdapterActivity(countryList, songPaths, ::playSong, ::openSongDetailsActivity)
    }

    private fun playSong(songPaths: List<String>, position: Int) {
        // Lógica para reproducir la canción en la posición dada
    }

    private fun openSongDetailsActivity() {
        // Lógica para abrir los detalles de la canción
    }
}
