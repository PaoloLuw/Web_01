package com.luwliette.ztmelody_02

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.luwliette.ztmelody_02.database.Song
import com.luwliette.ztmelody_02.database.SongDatabase
import com.luwliette.ztmelody_02.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultar la ActionBar
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_artist
            )
        )
        navView.setupWithNavController(navController)

        // Configurar el ImageButton para mostrar el PopupMenu
        val imageButton: ImageButton = findViewById(R.id.imageButton)
        imageButton.setOnClickListener { view ->
            showPopupMenu(view)
        }

        // Configurar el ImageButton para lanzar SearchActivity al hacer clic
        binding.BUTTONSEARCH.setOnClickListener {
            val intent = Intent(this, SeekingForMusic::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = androidx.appcompat.widget.PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.obtion_nav_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem -> handleMenuItemClick(menuItem) }
        popupMenu.show()
    }

    private fun handleMenuItemClick(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_one -> {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                } else {
                    scanMusicFiles()
                }
                Toast.makeText(this, "Music scanned", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_two -> {
                Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_three -> {
                Toast.makeText(this, "Action Three clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

}