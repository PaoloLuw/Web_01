package com.luwliette.ztmelody_02.ui.suggested

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.luwliette.ztmelody_02.database.SongDatabase
import com.luwliette.ztmelody_02.databinding.FragmentArtistBinding
import com.luwliette.ztmelody_02.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentArtistBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
