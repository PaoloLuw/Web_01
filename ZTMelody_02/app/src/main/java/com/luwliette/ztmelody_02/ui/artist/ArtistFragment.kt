package com.luwliette.ztmelody_02.ui.artist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.luwliette.ztmelody_02.MusicControlActivity
import com.luwliette.ztmelody_02.MusicService
import com.luwliette.ztmelody_02.R
import com.luwliette.ztmelody_02.database.SongDatabase
import com.luwliette.ztmelody_02.databinding.FragmentArtistBinding

class ArtistFragment : Fragment() {

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val songDatabase = SongDatabase(requireContext())
        val songList = songDatabase.getAllSongs()
        val songPaths = songList.map { it.data }

        val buttonsLayout = binding.buttonsLayout

        // Iterar sobre la lista de canciones y crear un botón personalizado para cada una
        songList.forEachIndexed { index, song ->
            // Inflar el diseño personalizado del botón
            val buttonLayout = layoutInflater.inflate(R.layout.custom_button_layout, null) as LinearLayout

            // Obtener referencias a los elementos dentro del diseño personalizado
            val imageView = buttonLayout.findViewById<ImageView>(R.id.imageView)
            val titleTextView = buttonLayout.findViewById<TextView>(R.id.titleTextView)
            val artistTextView = buttonLayout.findViewById<TextView>(R.id.artistTextView)

            // Configurar los datos de la canción en los elementos del botón personalizado
            titleTextView.text = song.title
            artistTextView.text = song.artist
            // Aquí deberías cargar la imagen correspondiente a la canción en el imageView

            // Configurar el onClickListener para el botón personalizado
            buttonLayout.setOnClickListener {
                Log.d("ArtistFragment", "Button clicked: ${song.title}")
                playSong(songPaths, index)
                openSongDetailsActivity()
            }

            // Agregar el botón personalizado al layout de botones
            buttonsLayout.addView(buttonLayout)
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
        Log.d("ArtistFragment", "Starting service with song path: ${songPaths[songIndex]}")
        requireContext().startService(intent)
    }

    private fun openSongDetailsActivity() {
        val intent = Intent(requireContext(), MusicControlActivity::class.java)
        startActivity(intent)
    }
}
