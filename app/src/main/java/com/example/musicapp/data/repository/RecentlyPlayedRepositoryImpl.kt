package com.example.musicapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicapp.data.model.SongListData
import com.example.musicapp.data.api.ApiService

class RecentlyPlayedRepositoryImpl(var apiService: ApiService) : RecentlyPlayedRepository{

    private val recentlyPlayed = MutableLiveData<SongListData>()

    val recentlyPlayedLivaData: LiveData<SongListData>
        get() = recentlyPlayed

    override suspend fun getAllSongs(): SongListData? {
        val result = apiService.getRecentlyPlayedSongListFromServer();
       /* result.let {
            recentlyPlayed.postValue(it.body())
        }*/

        return result.body()

    }
}