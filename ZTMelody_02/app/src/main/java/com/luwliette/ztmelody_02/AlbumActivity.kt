package com.luwliette.ztmelody_02

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luwliette.ztmelody_02.database.SongDatabase

class AlbumActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var musicService: MusicService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
            // Llamar a getCurrentSongPath() después de vincular el servicio
            val currentSongPath = musicService?.getCurrentSongPath()
            Log.d("CurrentSongPath", "The current song path is: $currentSongPath")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
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
        val countryList = ArrayList<SongModelActivity>()

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
            countryList.add(SongModelActivity(song.title, R.drawable.ic_music_list))
        }

        recyclerView.adapter = SongAdapterActivity(countryList, songPaths, ::playSong, ::openSongDetailsActivity)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    private fun playSong(songPaths: List<String>, songIndex: Int) {
        // Log para ver los datos que se están enviando
        Log.d("playSongChek", "Playing song at index: $songIndex, Song list: $songPaths")

        val intent = Intent(this, MusicService::class.java).apply {
            putStringArrayListExtra("SONG_LIST", ArrayList(songPaths))
            putExtra("SONG_INDEX", songIndex)
        }
        startService(intent)

        // Vincula el servicio si no está vinculado aún
        if (!isBound) {
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            // Si ya está vinculado, obten el path de la canción actual
            val currentSongPath = musicService?.getCurrentSongPath()
            Log.d("CurrentSongPath", "The current song path is: $currentSongPath")
        }
    }

    private fun openSongDetailsActivity() {
        val intent = Intent(this, MusicControlActivity_SCN::class.java)
        startActivity(intent)
    }
}
