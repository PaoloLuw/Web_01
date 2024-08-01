package com.luwliette.ztmelody_02

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.TimeUtils.formatDuration
import com.luwliette.ztmelody_02.database.FavoriteSong
import com.luwliette.ztmelody_02.database.FavoriteSongsDatabase
import com.luwliette.ztmelody_02.database.SongDatabase
import com.luwliette.ztmelody_02.databinding.ActivityMusicControlBinding

class MusicControlActivity_SCN : AppCompatActivity() {

    private var musicService: MusicService? = null
    private var isBound = false
    private lateinit var binding: ActivityMusicControlBinding
    private var isPlaying = true
    private lateinit var playPauseButton: Button
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var forwardButton: Button
    private lateinit var rewindButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var songNameTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var addFavoriteButton: Button

    private lateinit var songDatabase: SongDatabase
    private lateinit var favoriteSongsDatabase: FavoriteSongsDatabase

    private var currentSongId: Long = 0
    private var currentSongTitle: String = ""
    private var currentSongArtist: String = ""
    private var currentSongData: String = ""
    private var currentSongDateAdded: Long = 0

    // BroadcastReceiver para recibir actualizaciones del servicio de música
    private val musicReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val duration = it.getIntExtra(MusicService.EXTRA_DURATION, 0)
                val currentPosition = it.getIntExtra(MusicService.EXTRA_CURRENT_POSITION, 0)
                val songPath = it.getStringExtra(MusicService.EXTRA_SONG_PATH)

                Log.d("MusicControlActivity_SCN", "Received update: duration=$duration, currentPosition=$currentPosition, songPath=$songPath")

                if (duration > 0) {
                    seekBar.max = duration
                    seekBar.progress = currentPosition
                    timeTextView.text = formatDurationCustom(currentPosition) + " / " + formatDurationCustom(duration)
                }

                songPath?.let { path ->
                    updateSongName(extractSongNameFromPath(path)) // Llamada a extractSongNameFromPath
                }
            }
        }
    }



    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true

            // Obtener el path de la canción actual y mostrar el nombre
            val currentSongPath = musicService?.getCurrentSongPath()
            currentSongPath?.let { updateSongName(extractSongNameFromPath(it)) }
            Log.d("CurrentSongPath", "The current song path is: $currentSongPath")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMusicControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playPauseButton = binding.playPauseButton
        nextButton = binding.nextButton
        prevButton = binding.prevButton
        forwardButton = binding.forwardButton
        rewindButton = binding.rewindButton
        seekBar = binding.seekBar
        songNameTextView = binding.songNameTextView
        timeTextView = binding.timeTextView
        addFavoriteButton = binding.addFavoriteButton

        songDatabase = SongDatabase(this)
        favoriteSongsDatabase = FavoriteSongsDatabase(this)

        playPauseButton.setOnClickListener {
            isPlaying = !isPlaying
            updatePlayPauseButton()
            sendCommandToService(MusicService.ACTION_PLAY_PAUSE)
        }

        nextButton.setOnClickListener {
            sendCommandToService(MusicService.ACTION_NEXT)
            Handler(Looper.getMainLooper()).postDelayed({
                val currentSongPath = musicService?.getCurrentSongPath()
                currentSongPath?.let { path ->
                    updateSongName(extractSongNameFromPath(path))
                }
            }, 200) // Retardo de 200 ms
        }

        prevButton.setOnClickListener {
            sendCommandToService(MusicService.ACTION_PREV)
            Handler(Looper.getMainLooper()).postDelayed({
                val currentSongPath = musicService?.getCurrentSongPath()
                currentSongPath?.let { path ->
                    updateSongName(extractSongNameFromPath(path))
                }
            }, 200) // Retardo de 200 ms
        }

        forwardButton.setOnClickListener { sendCommandToService(MusicService.ACTION_FORWARD) }
        rewindButton.setOnClickListener { sendCommandToService(MusicService.ACTION_REWIND) }

        addFavoriteButton.setOnClickListener {

            val songTitle = songNameTextView.text.toString()

            // Obtener la canción por título
            val song = songDatabase.getSongByTitle(songTitle)
            Log.d("Problem of my communication", "Add/Remove favorite button clicked. Song title: $song")
            if (song != null) {
                if (favoriteSongsDatabase.isFavorite(song.id)) {
                    favoriteSongsDatabase.removeFavoriteSong(song.id)
                    updateFavoriteButton(false)
                    Log.d("FavoritesPrint", "Removed from favorites: ${song.title} by ${song.artist}")
                    showToast("${song.title} removed from favorites")
                } else {
                    val favoriteSong = FavoriteSong(song.id, song.title, song.artist, song.data, song.dateAdded)
                    favoriteSongsDatabase.addFavoriteSong(favoriteSong)
                    updateFavoriteButton(true)
                    Log.d("FavoritesPrint", "Added to favorites: ${song.title} by ${song.artist}")
                    showToast("${song.title} added to favorites")
                }

                // Mostrar todos los favoritos actuales
                val allFavorites = favoriteSongsDatabase.getAllFavoriteSongs()
                Log.d("Favorites", "Current favorite songs:")
                allFavorites.forEach { favorite ->
                    Log.d("FavoritesPrint", "Title: ${favorite.title}, Artist: ${favorite.artist}")
                }
            } else {
                Log.d("Error", "Song not found in the database")
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    sendSeekCommandToService(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val filter = IntentFilter().apply {
            addAction(MusicService.ACTION_UPDATE_UI)
        }
        registerReceiver(musicReceiver, filter)

        // Usar Handler para presionar el botón dos veces después de un breve retraso
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            simulateButtonClick()
            handler.postDelayed({
                simulateButtonClick()
            }, 10)
        }, 10)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(musicReceiver)
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

    private fun sendCommandToService(action: String) {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        startService(intent)
    }

    private fun sendSeekCommandToService(position: Int) {
        val intent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_SEEK_TO
            putExtra(MusicService.EXTRA_SEEK_POSITION, position)
        }
        startService(intent)
    }

    private fun updateSongName(name: String) {
        songNameTextView.text = name
    }

    private fun updateSongDetails(songName: String) {
        songNameTextView.text = songName
        // Aquí, extrae la información de la canción desde la base de datos
        val song = songDatabase.getSongById(currentSongId)
        song?.let {
            currentSongId = it.id
            currentSongTitle = it.title
            currentSongArtist = it.artist
            currentSongData = it.data
            currentSongDateAdded = it.dateAdded
            updateFavoriteButton(favoriteSongsDatabase.isFavorite(currentSongId))
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val drawable = if (isFavorite) R.drawable.ic_nothing else R.drawable.ic_nothing
        addFavoriteButton.setBackgroundResource(drawable)
    }

    private fun updatePlayPauseButton() {
        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        playPauseButton.setBackgroundResource(iconRes)
    }

    private fun simulateButtonClick() {
        playPauseButton.performClick()
    }

    // Función para mostrar un Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Función para extraer el nombre de la canción desde el path
    private fun extractSongNameFromPath(path: String): String {
        return path.substringAfterLast("/")
    }

    private fun formatDurationCustom(duration: Int): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

}
