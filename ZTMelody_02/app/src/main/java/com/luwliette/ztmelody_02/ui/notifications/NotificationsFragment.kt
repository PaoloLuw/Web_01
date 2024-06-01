package com.luwliette.ztmelody_02.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luwliette.ztmelody_02.AlbumActivity
import com.luwliette.ztmelody_02.CountryAdapterActivity_2
import com.luwliette.ztmelody_02.CountryModelActivity
import com.luwliette.ztmelody_02.R
import com.luwliette.ztmelody_02.database.SongDatabase

class NotificationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Configurar insets de la ventana
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2) // Configura GridLayoutManager

        val songDatabase = SongDatabase(requireContext())

        val artistList = songDatabase.getAllArtists()

        val countryList = artistList.map { artist ->
            CountryModelActivity(artist, R.drawable.ic_music_list)
        }

        recyclerView.adapter = CountryAdapterActivity_2(countryList) { artistName ->
            openArtistDetailsActivity(artistName)
        }
        return view
    }

    private fun openArtistDetailsActivity(artist: String) {
        Log.d("CHECK", "Opening AlbumActivity with artist: $artist")

        val intent = Intent(requireContext(), AlbumActivity::class.java).apply {
            putExtra("ARTIST_NAME", artist)
        }
        startActivity(intent)
    }
}
