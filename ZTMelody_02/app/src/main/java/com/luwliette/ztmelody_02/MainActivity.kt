package com.luwliette.ztmelody_02

import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
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
                Toast.makeText(this, "Action One clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_two -> {
                Toast.makeText(this, "Action Two clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_three -> {
                Toast.makeText(this, "Action Three clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }
}