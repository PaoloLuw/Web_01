package com.luwliette.ztmelody_02

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luwliette.ztmelody_02.database.SongDatabase

class AlbumActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_album)

        val artistName = intent.getStringExtra("ARTIST_NAME") ?: return //RECIVIMOS EL NOMBRE DEL ARTIST
        Log.d("ALBUM", "Received artist name: $artistName")

        if (artistName == null) {
            Log.e("ALBUM", "No artist name received!")
            return
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        val songDatabase = SongDatabase(this)
        val songList = songDatabase.getSongsByArtist(artistName)
        val songPaths = songList.map { it.data }
        val countryList = ArrayList<CountryModelActivity>()


        // Verificar si la lista de canciones está vacía o no
        if (songList.isEmpty()) {
            Log.e("ALBUMM", "No songs found for artist: $artistName")
            return
        } else {
            Log.d("ALBUMM", "Number of songs found: ${songList.size}")
            songList.forEach { song ->
                Log.d("ALBUMM", "Song: ${song.title}, Path: ${song.data}")
            }
        }


        songList.forEach { song ->
            countryList.add(CountryModelActivity(song.title, R.drawable.ic_music_list))
        }

        recyclerView.adapter = CountryAdapterActivity(countryList, songPaths, ::playSong, ::openSongDetailsActivity)
    }

    private fun playSong(songPaths: List<String>, songIndex: Int) {
        val intent = Intent(this, MusicService::class.java).apply {
            putStringArrayListExtra("SONG_LIST", ArrayList(songPaths))
            putExtra("SONG_INDEX", songIndex)
        }
        startService(intent)
    }

    private fun openSongDetailsActivity() {
        val intent = Intent(this, MusicControlActivity::class.java)
        startActivity(intent)
    }

}
