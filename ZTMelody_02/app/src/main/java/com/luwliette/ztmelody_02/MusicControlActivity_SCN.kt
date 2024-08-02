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

    private val updateInterval: Long = 300 // Intervalo de actualización en milisegundos
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateSongInfo()
            handler.postDelayed(this, updateInterval)
        }
    }

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
                    val songName = extractSongNameFromPath(path)
                    updateSongName(songName)
                    Log.d("ProblemCommunication", "Received update: duration=$duration, currentPosition=$currentPosition, songName=$songName")
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
            currentSongPath?.let {
                updateSongName(extractSongNameFromPath(it))

                // Verificar si la canción está en favoritos y actualizar el botón
                val song = songDatabase.getSongByPath(it)
                song?.let {
                    currentSongId = song.id
                    updateFavoriteButton(favoriteSongsDatabase.isFavorite(currentSongId))
                }
            }
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
        }

        prevButton.setOnClickListener {
            sendCommandToService(MusicService.ACTION_PREV)
        }

        forwardButton.setOnClickListener { sendCommandToService(MusicService.ACTION_FORWARD) }
        rewindButton.setOnClickListener { sendCommandToService(MusicService.ACTION_REWIND) }

        addFavoriteButton.setOnClickListener {
            // Obtener el camino completo de la canción actual desde el servicio
            val currentSongPath = musicService?.getCurrentSongPath()
            Log.d("MusicControlActivity_SCND", "Current song path: $currentSongPath")

            if (currentSongPath != null) {
                // Obtener la canción desde la base de datos usando el path completo
                val song = songDatabase.getSongByPath(currentSongPath)
                Log.d("MusicControlActivity_SCND", "Song fetched from database: $song")

                if (song != null) {
                    if (favoriteSongsDatabase.isFavorite(song.id)) {
                        Log.d("MusicControlActivity_SCND", "Song is already in favorites, removing it.")
                        favoriteSongsDatabase.removeFavoriteSong(song.id)
                        updateFavoriteButton(false)
                        showToast("${song.title} removed from favorites")
                        Log.d("MusicControlActivity_SCND", "Removed from favorites: ${song.title}")
                    } else {
                        Log.d("MusicControlActivity_SCND", "Song is not in favorites, adding it.")
                        val favoriteSong = FavoriteSong(
                            song.id, song.title, song.artist, currentSongPath, song.dateAdded
                        )
                        favoriteSongsDatabase.addFavoriteSong(favoriteSong)
                        updateFavoriteButton(true)
                        showToast("${song.title} added to favorites")
                        Log.d("MusicControlActivity_SCND", "Added to favorites: ${song.title}")
                    }
                    // Imprimir toda la base de datos de canciones favoritas
                    printAllFavoriteSongs()
                } else {
                    showToast("Song not found in the database")
                    Log.d("MusicControlActivity_SCND", "Song not found in the database for path: $currentSongPath")
                }
            } else {
                showToast("Current song path not available")
                Log.d("MusicControlActivity_SCND", "Current song path not available")
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

    private fun printAllFavoriteSongs() {
        val favoriteSongs = favoriteSongsDatabase.getAllFavoriteSongs()
        Log.d("FavoriteSongsDatabase", "All favorite songs:")
        for (favoriteSong in favoriteSongs) {
            Log.d("FavoriteSongsDatabase", "ID: ${favoriteSong.id}, Title: ${favoriteSong.title}, Artist: ${favoriteSong.artist}, Data: ${favoriteSong.data}, Date Added: ${favoriteSong.dateAdded}")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(musicReceiver)
        handler.removeCallbacks(updateRunnable)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        handler.post(updateRunnable)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        handler.removeCallbacks(updateRunnable)
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

    private fun updateSongInfo() {
        val songPath = musicService?.getCurrentSongPath()
        songPath?.let { path ->
            updateSongName(extractSongNameFromPath(path))
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val drawable = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_nofavorite
        addFavoriteButton.setBackgroundResource(drawable)
    }

    private fun updatePlayPauseButton() {
        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        playPauseButton.setBackgroundResource(iconRes)
    }

    private fun simulateButtonClick() {
        playPauseButton.performClick()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun extractSongNameFromPath(path: String): String {
        val fileName = path.substringAfterLast("/")
        return fileName.substringBeforeLast(".")
    }

    private fun formatDurationCustom(duration: Int): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
