package com.luwliette.ztmelody_02.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luwliette.ztmelody_02.SongAdapterActivity
import com.luwliette.ztmelody_02.SongModelActivity
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
        //recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) // Configura LinearLayoutManager en horizontal


        val songDatabase = SongDatabase(requireContext())
        val songList = songDatabase.getAllSongs()
        val songPaths = songList.map { it.data }



        val MusicList = ArrayList<SongModelActivity>()

        songList.forEach { song ->
            MusicList.add(SongModelActivity(song.title, R.drawable.icon_normal))
        }
        recyclerView.adapter = SongAdapterActivity(MusicList, songPaths, ::playSong, ::openSongDetailsActivity)

        return view
    }

    private fun playSong(songPaths: List<String>, songIndex: Int) {

        Log.d("playSongCheck", "Playing song at index: $songIndex, Song list: $songPaths")

        val intent = Intent(requireContext(), MusicService::class.java).apply {
            putStringArrayListExtra("SONG_LIST", ArrayList(songPaths))
            putExtra("SONG_INDEX", songIndex)
        }
        requireContext().startService(intent)
    }

    private fun openSongDetailsActivity() {
        //aqui que busque que se esta reproduciendo
        val intent = Intent(requireContext(), MusicControlActivity::class.java)
        startActivity(intent)
    }
}
