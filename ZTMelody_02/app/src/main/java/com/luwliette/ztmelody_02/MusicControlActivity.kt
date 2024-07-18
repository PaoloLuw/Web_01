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

class MusicControlActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicControlBinding
    private var isPlaying = false
    // Declaración de variables
    private lateinit var playPauseButton: Button
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var forwardButton: Button
    private lateinit var rewindButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var songNameTextView: TextView // Nuevo: TextView para el nombre de la canción
    private lateinit var timeTextView: TextView // Nuevo: TextView para el tiempo transcurrido

    // BroadcastReceiver para recibir actualizaciones del servicio de música
    private val musicReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val duration = it.getIntExtra(MusicService.EXTRA_DURATION, 0)
                val currentPosition = it.getIntExtra(MusicService.EXTRA_CURRENT_POSITION, 0)
                val songName = it.getStringExtra(MusicService.EXTRA_SONG_NAME) // Nuevo: obtener el nombre de la canción
                if (duration > 0) {
                    seekBar.max = duration
                    seekBar.progress = currentPosition
                    timeTextView.text = formatDuration(currentPosition) + " / " + formatDuration(duration)
                }
                // Nuevo: actualizar el TextView del nombre de la canción
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
        binding = ActivityMusicControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización de vistas
        playPauseButton = binding.playPauseButton
        nextButton = binding.nextButton
        prevButton = binding.prevButton
        forwardButton = binding.forwardButton
        rewindButton = binding.rewindButton
        seekBar = binding.seekBar
        songNameTextView = binding.songNameTextView // Nuevo: inicializar el TextView para el nombre de la canción
        timeTextView = binding.timeTextView // Nuevo: inicializar el TextView para el tiempo transcurrido

        // Inicializar el nombre de la canción con un texto predeterminado
        updateSongName("Nombre de la canción")

        // Configuración de listeners para los botones y la barra de progreso
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

        // Registro del BroadcastReceiver para recibir actualizaciones del servicio de música
        val filter = IntentFilter().apply {
            addAction(MusicService.ACTION_UPDATE_UI)
        }
        registerReceiver(musicReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Desregistro del BroadcastReceiver al destruir la actividad
        unregisterReceiver(musicReceiver)
    }

    // Función para enviar comandos al servicio de música
    private fun sendCommandToService(action: String) {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        startService(intent)
    }

    // Función para enviar comandos de búsqueda al servicio de música
    private fun sendSeekCommandToService(position: Int) {
        val intent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_SEEK_TO
            putExtra(MusicService.EXTRA_SEEK_POSITION, position)
        }
        startService(intent)
    }

    // Función para actualizar el nombre de la canción en el TextView correspondiente
    private fun updateSongName(name: String) {
        songNameTextView.text = name
    }

    private fun updatePlayPauseButton() {
        val animatorSet: AnimatorSet = if (isPlaying) {
            AnimatorInflater.loadAnimator(this, R.animator.play_to_pause) as AnimatorSet
        } else {
            AnimatorInflater.loadAnimator(this, R.animator.pause_to_play) as AnimatorSet
        }
        animatorSet.setTarget(playPauseButton)
        animatorSet.start()
    }
    private fun updatePlayPauseButton2() {
        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        playPauseButton.setBackgroundResource(iconRes)
    }

}
