package com.luwliette.ztmelody_02.ui.artist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.luwliette.ztmelody_02.MusicService
import com.luwliette.ztmelody_02.R
import com.luwliette.ztmelody_02.databinding.FragmentArtistBinding
import com.luwliette.ztmelody_02.database.FavoriteSongsDatabase
import com.luwliette.ztmelody_02.database.SongDatabase
import com.luwliette.ztmelody_02.database.FavoriteSong

class ArtistFragment : Fragment() {

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

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

    private val musicReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val duration = it.getIntExtra(MusicService.EXTRA_DURATION, 0)
                val currentPosition = it.getIntExtra(MusicService.EXTRA_CURRENT_POSITION, 0)
                val songName = it.getStringExtra(MusicService.EXTRA_SONG_NAME)

                Log.d("ArtistFragment", "Received update: duration=$duration, currentPosition=$currentPosition, songName=$songName")

                if (duration > 0) {
                    seekBar.max = duration
                    seekBar.progress = currentPosition
                    timeTextView.text = formatDuration(currentPosition) + " / " + formatDuration(duration)
                }
                songName?.let { updateSongDetails(it) }
            }
        }
    }

    private fun formatDuration(duration: Int): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playPauseButton = binding.playPauseButton
        nextButton = binding.nextButton
        prevButton = binding.prevButton
        forwardButton = binding.forwardButton
        rewindButton = binding.rewindButton
        seekBar = binding.seekBar
        songNameTextView = binding.songNameTextView
        timeTextView = binding.timeTextView
        addFavoriteButton = binding.addFavoriteButton

        songDatabase = SongDatabase(requireContext())
        favoriteSongsDatabase = FavoriteSongsDatabase(requireContext())

        updateSongName("Nombre de la canción")

        playPauseButton.setOnClickListener {
            isPlaying = !isPlaying
            updatePlayPauseButton2()
            sendCommandToService(MusicService.ACTION_PLAY_PAUSE)
        }
        nextButton.setOnClickListener { sendCommandToService(MusicService.ACTION_NEXT) }
        prevButton.setOnClickListener { sendCommandToService(MusicService.ACTION_PREV) }
        forwardButton.setOnClickListener { sendCommandToService(MusicService.ACTION_FORWARD) }
        rewindButton.setOnClickListener { sendCommandToService(MusicService.ACTION_REWIND) }

        addFavoriteButton.setOnClickListener {
            val songTitle = songNameTextView.text.toString()
            Log.d("ArtistFragment", "Add/Remove favorite button clicked. Song title: $songTitle")

            // Obtener la canción por título
            val song = songDatabase.getSongByTitle(songTitle)

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
        requireContext().registerReceiver(musicReceiver, filter)

        // Usar Handler para presionar el botón dos veces después de un breve retraso
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            simulateButtonClick()
            handler.postDelayed({
                simulateButtonClick()
            }, 10) // Retraso de 10 ms entre los dos clics
        }, 10) // Retraso de 10 ms después de que la vista se cargue
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(musicReceiver)
        _binding = null
    }

    private fun sendCommandToService(action: String) {
        val intent = Intent(requireContext(), MusicService::class.java).apply {
            this.action = action
        }
        requireContext().startService(intent)
    }

    private fun sendSeekCommandToService(position: Int) {
        val intent = Intent(requireContext(), MusicService::class.java).apply {
            action = MusicService.ACTION_SEEK_TO
            putExtra(MusicService.EXTRA_SEEK_POSITION, position)
        }
        requireContext().startService(intent)
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
        val drawable = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_nofavorite
        addFavoriteButton.setBackgroundResource(drawable)
    }

    private fun updatePlayPauseButton2() {
        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        playPauseButton.setBackgroundResource(iconRes)
    }

    private fun simulateButtonClick() {
        playPauseButton.performClick()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
