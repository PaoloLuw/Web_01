package com.luwliette.ztmelody_02

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.luwliette.ztmelody_02.database.FavoriteSong
import com.luwliette.ztmelody_02.databinding.ActivityFavoriteSongsBinding
import com.luwliette.ztmelody_02.database.FavoriteSongsDatabase

class FavoriteSongsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteSongsBinding
    private val songViewModel: SongViewModel by viewModels()
    private lateinit var adapter: SongAdapterClass_3
    private lateinit var favoriteSongList: List<FavoriteSong>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        enableEdgeToEdge()
        binding = ActivityFavoriteSongsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView setup
        adapter = SongAdapterClass_3(arrayListOf(), ::playSong, ::openSongDetailsActivity)
        binding.favoriteSongsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favoriteSongsRecyclerView.adapter = adapter

        // ViewModel setup (optional, if you're using it to observe data)
        // songViewModel.songList.observe(this, Observer { songs ->
        //     adapter.updateSongs(songs)
        // })
    }

    override fun onResume() {
        super.onResume()
        updateFavoriteSongs()
    }

    private fun updateFavoriteSongs() {
        val favoriteSongDatabase = FavoriteSongsDatabase(this)
        favoriteSongList = favoriteSongDatabase.getAllFavoriteSongs()
        val songPaths = favoriteSongList.map { it.data }
        val musicList = ArrayList<SongModelActivity>()

        // Verificar si la lista de canciones favoritas está vacía o no
        if (favoriteSongList.isEmpty()) {
            Log.e("FavoriteSongsActivity", "No favorite songs found!")
        } else {
            favoriteSongList.forEach { song ->
                Log.d("FavoriteSongsActivity", "Favorite song: ${song.title}, Path: ${song.data}")
                musicList.add(SongModelActivity(song.title, R.drawable.icon_normal))
            }
        }

        // Actualizar el adaptador con la nueva lista de canciones
        adapter.updateSongs(musicList)
        songViewModel.setSongList(musicList) // Actualizar el ViewModel si es necesario
    }

    private fun getOriginalIndex(filteredSong: SongModelActivity): Int {
        return favoriteSongList.indexOfFirst { it.title == filteredSong.countryName }
    }

    private fun playSong(filteredSongs: List<SongModelActivity>, position: Int) {
        val filteredSong = filteredSongs[position]
        val songIndex = getOriginalIndex(filteredSong)
        if (songIndex != -1) {
            Log.d("FavoriteSongsActivity", "Playing song at index: $songIndex")
            val songPaths = favoriteSongList.map { it.data }
            val intent = Intent(this, MusicService::class.java).apply {
                putStringArrayListExtra("SONG_LIST", ArrayList(songPaths))
                putExtra("SONG_INDEX", songIndex)
            }
            startService(intent)
        } else {
            Log.e("FavoriteSongsActivity", "Song index not found!")
        }
    }

    private fun openSongDetailsActivity() {
        val intent = Intent(this, MusicControlActivity_SCN::class.java)
        startActivity(intent)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
