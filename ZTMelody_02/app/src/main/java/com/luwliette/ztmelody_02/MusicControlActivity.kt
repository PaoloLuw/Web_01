package com.luwliette.ztmelody_02

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.luwliette.ztmelody_02.databinding.ActivityMusicControlBinding

import android.os.Handler
import android.os.Looper

class MusicControlActivity : AppCompatActivity() {

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

    // BroadcastReceiver para recibir actualizaciones del servicio de música
    private val musicReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val duration = it.getIntExtra(MusicService.EXTRA_DURATION, 0)
                val currentPosition = it.getIntExtra(MusicService.EXTRA_CURRENT_POSITION, 0)
                val songName = it.getStringExtra(MusicService.EXTRA_SONG_NAME)
                if (duration > 0) {
                    seekBar.max = duration
                    seekBar.progress = currentPosition
                    timeTextView.text = formatDuration(currentPosition) + " / " + formatDuration(duration)
                }
                songName?.let { updateSongName(it) }
            }
        }
    }

    // Función para formatear la duración en minutos y segundos
    private fun formatDuration(duration: Int): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
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
            }, 10) // Retraso de 200 ms entre los dos clics
        }, 10) // Retraso de 500 ms después de que la actividad se cargue
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(musicReceiver)
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

    private fun updatePlayPauseButton2() {
        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        playPauseButton.setBackgroundResource(iconRes)
    }

    private fun simulateButtonClick() {
        playPauseButton.performClick()
    }
}

