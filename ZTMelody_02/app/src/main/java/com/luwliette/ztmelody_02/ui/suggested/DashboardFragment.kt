package com.luwliette.ztmelody_02.ui.suggested

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
import com.luwliette.ztmelody_02.MusicControlActivity
import com.luwliette.ztmelody_02.MusicService
import com.luwliette.ztmelody_02.R
import com.luwliette.ztmelody_02.SongAdapterClass_3
import com.luwliette.ztmelody_02.SongModelActivity
import com.luwliette.ztmelody_02.database.SongDatabase
import com.luwliette.ztmelody_02.databinding.FragmentArtistBinding

class DashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var recyclerView3: RecyclerView

    private var _binding: FragmentArtistBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Configurar insets de la ventana
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recyclerView2 = view.findViewById(R.id.recyclerView2)
        recyclerView2.layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false)

        recyclerView3 = view.findViewById(R.id.recyclerView3)
        recyclerView3.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false)


        // Inicialización de RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Obtener canciones aleatorias de la base de datos
        val songMixedDatabase = SongDatabase(requireContext())
        val randomSongList = songMixedDatabase.getRandomSongs(30) // Por ejemplo, obtener 10 canciones aleatorias

        // Crear la lista de modelos de canción aleatoria
        val randomMusicList = ArrayList<SongModelActivity>()
        randomSongList.forEach { song ->
            randomMusicList.add(SongModelActivity(song.title, R.drawable.icon_normal))
        }

        // Asignar adaptador al RecyclerView con las canciones aleatorias
        recyclerView.adapter = SongAdapterClass_3(randomMusicList, ::playSong, ::openSongDetailsActivity)

        val songDatabase = SongDatabase(requireContext())
        val songList = songDatabase.getAllSongs()
        val songPaths = songList.map { it.data }
        val MusicList = ArrayList<SongModelActivity>()

        songList.forEach { song ->
            MusicList.add(SongModelActivity(song.title, R.drawable.icon_normal))
        }

        recyclerView2.adapter = SongAdapterClass_3(MusicList, ::playSong, ::openSongDetailsActivity)

        // Filtrar canciones agregadas en los últimos 12 días para recyclerView3
        val recentSongList = songDatabase.getRecentSongs(30)
        val recentMusicList = ArrayList<SongModelActivity>()

        recentSongList.forEach { song ->
            recentMusicList.add(SongModelActivity(song.title, R.drawable.icon_normal))
        }

        recyclerView3.adapter = SongAdapterClass_3(recentMusicList, ::playSong, ::openSongDetailsActivity)

        return view
    }

    private fun playSong(filteredSongs: List<SongModelActivity>, position: Int) {
        val filteredSong = filteredSongs[position]
        val songDatabase = SongDatabase(requireContext())
        val songList = songDatabase.getAllSongs()
        val songIndex = songList.indexOfFirst { it.title == filteredSong.countryName }
        if (songIndex != -1) {
            val songPaths = songList.map { it.data }
            Log.d("DashboardFragment", "Playing song at index: $songIndex")
            val intent = Intent(requireContext(), MusicService::class.java).apply {
                putStringArrayListExtra("SONG_LIST", ArrayList(songPaths))
                putExtra("SONG_INDEX", songIndex)
            }
            requireContext().startService(intent)
        } else {
            Log.e("DashboardFragment", "Song index not found!")
        }
    }

    private fun openSongDetailsActivity() {
        val intent = Intent(requireContext(), MusicControlActivity::class.java)
        startActivity(intent)
    }
}
