package com.luwliette.ztmelody_02

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.luwliette.ztmelody_02.database.SongDatabase

class MusicService : Service() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.luwliette.ztmelody_02.PLAY_PAUSE"
        const val ACTION_NEXT = "com.luwliette.ztmelody_02.NEXT"
        const val ACTION_PREV = "com.luwliette.ztmelody_02.PREV"
        const val ACTION_FORWARD = "com.luwliette.ztmelody_02.FORWARD"
        const val ACTION_REWIND = "com.luwliette.ztmelody_02.REWIND"
        const val ACTION_UPDATE_UI = "com.luwliette.ztmelody_02.UPDATE_UI"
        const val ACTION_SEEK_TO = "com.luwliette.ztmelody_02.SEEK_TO"
        const val EXTRA_SEEK_POSITION = "seek_position"
        const val EXTRA_DURATION = "duration"
        const val EXTRA_CURRENT_POSITION = "current_position"

        const val EXTRA_SONG_NAME = "song_name" // Nueva constante para el nombre de la canción
        const val EXTRA_SONG_ID = "com.luwliette.ztmelody_02.EXTRA_SONG_ID" // Nueva constante para el ID de la canción
        const val ACTION_PLAY_RANDOM = "com.luwliette.ztmelody_02.ACTION_PLAY_RANDOM" // Nueva constante para reproducción aleatoria
        const val EXTRA_SONG_PATH = "com.luwliette.ztmelody_02.EXTRA_SONG_PATH"
        const val ACTION_PLAY = "com.luwliette.ztmelody_02.action.PLAY"


    }

    private var mediaPlayer: MediaPlayer? = null
    private var songList: List<String> = emptyList()
    private var currentSongIndex: Int = 0
    private val handler = Handler()
    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                val intent = Intent(ACTION_UPDATE_UI).apply {
                    putExtra(EXTRA_CURRENT_POSITION, it.currentPosition)
                    putExtra(EXTRA_DURATION, it.duration)
                }
                sendBroadcast(intent)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> togglePlayPause()
            ACTION_NEXT -> playNextSong()
            ACTION_PREV -> playPreviousSong()
            ACTION_FORWARD -> forwardSong()
            ACTION_REWIND -> rewindSong()
            ACTION_SEEK_TO -> seekTo(intent.getIntExtra(EXTRA_SEEK_POSITION, 0))
            else -> {
                songList = intent?.getStringArrayListExtra("SONG_LIST") ?: emptyList()
                currentSongIndex = intent?.getIntExtra("SONG_INDEX", 0) ?: 0
                if (songList.isNotEmpty()) {
                    playSong(songList[currentSongIndex])
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(updateSeekBarTask)
        mediaPlayer?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun playSong(songPath: String) {
        Log.d("ArtistFragment", "ENTRE A PLAYSONG:")
        Log.d("ArtistFragmentpath", "Playing song from path: $songPath")
        try {
            mediaPlayer?.reset()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(songPath)
                prepare()
                start()
                setOnCompletionListener {
                    playNextSong()
                }
            }
            handler.post(updateSeekBarTask)
            updateUI()
        } catch (e: Exception) {
            Log.e("MusicService", "Error al reproducir la canción: ${e.message}", e)
        }
    }

    private fun playNextSong() {
        currentSongIndex = (currentSongIndex + 1) % songList.size
        playSong(songList[currentSongIndex])
    }

    private fun playPreviousSong() {
        currentSongIndex = if (currentSongIndex - 1 < 0) songList.size - 1 else currentSongIndex - 1
        playSong(songList[currentSongIndex])
    }

    private fun forwardSong() {
        mediaPlayer?.let {
            it.seekTo(it.currentPosition + 5000) // Avanza 5 segundos
        }
    }

    private fun rewindSong() {
        mediaPlayer?.let {
            it.seekTo(it.currentPosition - 5000) // Retrocede 5 segundos
        }
    }

    private fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.start()
            }
        }
        updateUI()
    }

    private fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    private fun updateUI() {
        val intent = Intent(ACTION_UPDATE_UI).apply {
            mediaPlayer?.let {
                putExtra(EXTRA_CURRENT_POSITION, it.currentPosition)
                putExtra(EXTRA_DURATION, it.duration)
                putExtra(EXTRA_SONG_NAME, getSongName()) // Agregar el nombre de la canción aquí
            }
        }
        sendBroadcast(intent)
    }
    private fun getSongName(): String? {
        val songDatabase = SongDatabase(this)
        val allSongs = songDatabase.getAllSongs()
        return if (currentSongIndex >= 0 && currentSongIndex < allSongs.size) {
            allSongs[currentSongIndex].title
        } else {
            null
        }
    }

    private fun playRandomSong() {
        val songDatabase = SongDatabase(this)
        val allSongs = songDatabase.getAllSongs()
        if (allSongs.isNotEmpty()) {
            val randomSong = allSongs.random()
            playSong(randomSong.data)
        }
    }

}