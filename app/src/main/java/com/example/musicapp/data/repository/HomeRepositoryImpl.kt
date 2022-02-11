package com.example.musicapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicapp.data.model.SongListData
import com.example.musicapp.data.api.ApiService

class HomeRepositoryImpl(var apiService: ApiService) : HomeRepository {

    private val currentlyPlayingX = MutableLiveData<SongListData>()

    val songListDataLivaData: LiveData<SongListData>
        get() = currentlyPlayingX

    override suspend fun getAllSongs(): SongListData {
        var result = apiService.getSongListFromServer();

//        result.let {
//            currentlyPlayingX.postValue(it.body())
//        }
        return result.body()!!
    }
}