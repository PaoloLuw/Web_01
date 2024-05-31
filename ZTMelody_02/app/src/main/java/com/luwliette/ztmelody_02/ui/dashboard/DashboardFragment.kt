package com.luwliette.ztmelody_02.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.luwliette.ztmelody_02.R
import com.luwliette.ztmelody_02.database.SongDatabase
import com.luwliette.ztmelody_02.databinding.FragmentDashboardBinding
import com.luwliette.ztmelody_02.MusicService

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val songDatabase = SongDatabase(requireContext())
        val songList = songDatabase.getAllSongs()
        val songPaths = songList.map { it.data }

        val buttonsLayout = binding.buttonsLayout

        // Iterar sobre la lista de canciones y crear un botón para cada una
        songList.forEachIndexed { index, song ->
            val button = Button(requireContext())
            button.text = song.title
            button.setOnClickListener {
                playSong(songPaths, index)
            }
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.COLOR_BASE_1))
            button.textSize = 12f
            button.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

            buttonsLayout.addView(button)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun playSong(songPaths: List<String>, songIndex: Int) {

        val intent = Intent(requireContext(), MusicService::class.java).apply {
            putStringArrayListExtra("SONG_LIST", ArrayList(songPaths))
            putExtra("SONG_INDEX", songIndex)
        }
        //Log.d("DASH", "ENTRE A AQUI DIOS $intent")

        requireContext().startService(intent)
    }
}
