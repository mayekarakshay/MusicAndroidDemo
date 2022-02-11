package com.example.musicapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.data.model.SongListData
import com.example.musicapp.data.repository.HomeRepository
import com.example.musicapp.data.repository.RecentlyPlayedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentlyPlayedViewModel(val repository: RecentlyPlayedRepository) : ViewModel() {

    private val recentlyPlayedData = MutableLiveData<SongListData>()


    val recentlyPlayedDataLivaData: LiveData<SongListData>
        get() = recentlyPlayedData

    fun fetchRecentlyPlayed() {
        CoroutineScope(Dispatchers.IO).launch {
            var result = repository.getAllSongs() as SongListData;
            result.let { recentlyPlayedData.postValue(result) }

        }

    }
}