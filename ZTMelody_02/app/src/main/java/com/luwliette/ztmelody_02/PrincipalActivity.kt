package com.luwliette.ztmelody_02

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luwliette.ztmelody_02.database.Song
import com.luwliette.ztmelody_02.database.SongDatabase

import com.luwliette.ztmelody_02.ui.WelcomeActivity
import com.luwliette.ztmelody_02.ui.pruebas.GoogleActivity

//import com.luwliette.ztmelody_02.ui.pruebas.PruevasActivity

class PrincipalActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ocultar la ActionBar
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)

        val sharedPreferences = getSharedPreferences("com.luwliette.ztmelody_02", MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            // Solicitar permisos la primera vez que se inicia la aplicación
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                scanMusicFiles()
            }
            // Establecer isFirstRun a false después de la primera ejecución
            sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
        }
        //else {
//            // Si no es la primera ejecución, ir directamente a MainActivity
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<Button>(R.id.btnNext)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

//        val scanButton = findViewById<Button>(R.id.btnScanMusic)
//        scanButton.setOnClickListener {
//            val intent = Intent(this, ScanMusicActivity::class.java)
//            startActivity(intent)
//        }

        val PRUEVA = findViewById<Button>(R.id.Google_btn)
        PRUEVA.setOnClickListener {
            val intent = Intent(this, GoogleActivity::class.java)
            startActivity(intent)
        }
    }

    private fun scanMusicFiles() {
        val songList = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val data = cursor.getString(dataColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)

                val song = Song(id, title, artist, data, dateAdded)
                songList.add(song)

                // Log para mostrar la canción escaneada en la consola
                Log.d("ScanMusicActivity", "Escaneado: $title by $artist")
            }
        }

        // Limpiar la base de datos antes de agregar las nuevas canciones
        val songDatabase = SongDatabase(this)
        songDatabase.clearAllSongs()

        // Agregar las nuevas canciones a la base de datos
        songList.forEach { songDatabase.addSong(it) }

        // Puedes imprimir los datos para ver que todo esté correcto
        songList.forEach {
            Log.d("ScanMusicActivity", "Song: $it")
        }
    }

}