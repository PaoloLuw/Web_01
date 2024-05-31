package com.luwliette.ztmelody_02.ui.pruebas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luwliette.ztmelody_02.AlbumActivity
import com.luwliette.ztmelody_02.R

class PruebaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_prueba)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button = findViewById<Button>(R.id.openAlbumActivityButton)
        button.setOnClickListener {
            val intent = Intent(this, AlbumActivity::class.java)
            intent.putExtra("ARTIST_NAME", "Delacey")
            startActivity(intent)
        }
    }
}