package com.luwliette.ztmelody_02

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SongViewModel : ViewModel() {
    private val _songList = MutableLiveData<List<SongModelActivity>>()
    val songList: LiveData<List<SongModelActivity>> = _songList

    private val _filteredSongList = MutableLiveData<List<SongModelActivity>>()
    val filteredSongList: LiveData<List<SongModelActivity>> = _filteredSongList

    fun setSongList(songs: List<SongModelActivity>) {
        _songList.value = songs
        _filteredSongList.value = songs
    }

    fun filterSongs(query: String) {
        _filteredSongList.value = if (query.isEmpty()) {
            _songList.value
        } else {
            _songList.value?.filter { it.countryName.contains(query, true) }
        }
    }
}
