package com.example.musicapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicapp.data.api.ApiHelper
import com.example.musicapp.data.model.SongListData

interface RecentlyPlayedRepository {
    suspend fun getAllSongs(): SongListData?
}