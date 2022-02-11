package com.example.musicapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.data.model.SongListData
import com.example.musicapp.data.repository.HomeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel( val homeRepository: HomeRepository) : ViewModel() {

    private val songListData = MutableLiveData<SongListData>()

    val songListDataLivaData: LiveData<SongListData>
        get() = songListData

    fun fetchSongList(){
        CoroutineScope(Dispatchers.IO).launch {
            val res = homeRepository.getAllSongs();
            songListData.postValue(res)

        }


    }

}