package com.luwliette.ztmelody_02.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luwliette.ztmelody_02.R
import com.luwliette.ztmelody_02.ScanMusicActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnContinue=findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {

            Toast.makeText(applicationContext,"Me Tocaste!!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}