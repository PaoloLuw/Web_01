package com.luwliette.ztmelody_02

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.luwliette.ztmelody_02.database.Song
import com.luwliette.ztmelody_02.database.SongDatabase
import com.luwliette.ztmelody_02.databinding.ActivitySeekingForMusicBinding

class SeekingForMusic : AppCompatActivity() {
    private lateinit var binding: ActivitySeekingForMusicBinding
    private val songViewModel: SongViewModel by viewModels()
    private lateinit var adapter: SongAdapterClass_3
    private lateinit var songList: List<Song>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySeekingForMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Delay to ensure the layout is fully inflated before requesting focus and showing the keyboard
        binding.editTextSearch.postDelayed({
            binding.editTextSearch.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.editTextSearch, InputMethodManager.SHOW_IMPLICIT)
        }, 200)

        // ViewModel setup
        val songDatabase = SongDatabase(this)
        songList = songDatabase.getAllSongs()
        val songPaths = songList.map { it.data }
        val musicList = ArrayList<SongModelActivity>()

        // RecyclerView setup
        adapter = SongAdapterClass_3(musicList, ::playSong, ::openSongDetailsActivity)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        songList.forEach { song ->
            musicList.add(SongModelActivity(song.title, R.drawable.icon_normal))
        }
        adapter.updateSongs(musicList)

        songViewModel.setSongList(musicList)

        songViewModel.filteredSongList.observe(this) { filteredSongs ->
            adapter.updateSongs(filteredSongs as ArrayList<SongModelActivity>)
        }

        // EditText setup for filtering
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                songViewModel.filterSongs(s.toString())
            }
        })
    }

    private fun getOriginalIndex(filteredSong: SongModelActivity): Int {
        return songList.indexOfFirst { it.title == filteredSong.countryName }
    }

    private fun playSong(filteredSongs: List<SongModelActivity>, position: Int) {
        val filteredSong = filteredSongs[position]
        val songIndex = getOriginalIndex(filteredSong)
        if (songIndex != -1) {
            Log.d("SeekingForMusicpp", "Playing song at index: $songIndex")
            val songPaths = songList.map { it.data }
            val intent = Intent(this, MusicService::class.java).apply {
                putStringArrayListExtra("SONG_LIST", ArrayList(songPaths))
                putExtra("SONG_INDEX", songIndex)
            }
            startService(intent)
        } else {
            Log.e("SeekingForMusicpp", "Song index not found!")
        }
    }

    private fun openSongDetailsActivity() {
        val intent = Intent(this, MusicControlActivity::class.java)
        startActivity(intent)
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
