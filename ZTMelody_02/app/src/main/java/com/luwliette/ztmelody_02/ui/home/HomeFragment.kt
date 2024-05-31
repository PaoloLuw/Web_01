package com.luwliette.ztmelody_02.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luwliette.ztmelody_02.CountryAdapterActivity
import com.luwliette.ztmelody_02.CountryAdapterActivity_2
import com.luwliette.ztmelody_02.CountryModelActivity
import com.luwliette.ztmelody_02.MusicControlActivity
import com.luwliette.ztmelody_02.MusicService
import com.luwliette.ztmelody_02.R
import com.luwliette.ztmelody_02.database.SongDatabase

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Configurar insets de la ventana
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //recyclerView = view.findViewById(R.id.recyclerView)
        //recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 1) // Configura GridLayoutManager

        val songDatabase = SongDatabase(requireContext())
        val songList = songDatabase.getAllSongs()
        val songPaths = songList.map { it.data }

        val countryList = ArrayList<CountryModelActivity>()

        songList.forEach { song ->
            countryList.add(CountryModelActivity(song.title, R.drawable.ic_music_list))
        }

        recyclerView.adapter = CountryAdapterActivity(countryList, songPaths, ::playSong, ::openSongDetailsActivity)

        return view
    }

    private fun playSong(songPaths: List<String>, songIndex: Int) {
        val intent = Intent(requireContext(), MusicService::class.java).apply {
            putStringArrayListExtra("SONG_LIST", ArrayList(songPaths))
            putExtra("SONG_INDEX", songIndex)
        }
        requireContext().startService(intent)
    }

    private fun openSongDetailsActivity() {
        val intent = Intent(requireContext(), MusicControlActivity::class.java)
        startActivity(intent)
    }
}
