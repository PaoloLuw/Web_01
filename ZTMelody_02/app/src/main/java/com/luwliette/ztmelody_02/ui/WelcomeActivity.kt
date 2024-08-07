package com.luwliette.ztmelody_02.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luwliette.ztmelody_02.PrincipalActivity
import com.luwliette.ztmelody_02.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val sharedPreferences = getSharedPreferences("com.luwliette.ztmelody_02", MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            // Si es la primera vez que se ejecuta, realizar acciones necesarias
            enableEdgeToEdge()
            setContentView(R.layout.activity_welcome)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            val btnContinue = findViewById<Button>(R.id.btnContinue)
            btnContinue.setOnClickListener {
                // Solicitar permisos de acceso a la memoria
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                } else {
                    // Permiso ya otorgado, continuar a PrincipalActivity
                    proceedToPrincipalActivity()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
            }
        } else {
            // Si no es la primera vez, ir directamente a PrincipalActivity
            proceedToPrincipalActivity()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun proceedToPrincipalActivity() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso otorgado, continuar a PrincipalActivity
                    proceedToPrincipalActivity()
                } else {
                    // Permiso denegado, manejar la situación apropiadamente
                    Toast.makeText(this, "Permiso denegado. No se puede continuar.", Toast.LENGTH_SHORT).show()
                    finish() // Terminar la actividad si se deniega el permiso
                }
            }
        }
    }
}

