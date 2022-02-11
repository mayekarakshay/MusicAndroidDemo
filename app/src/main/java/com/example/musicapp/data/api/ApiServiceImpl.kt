package com.example.musicapp.data.api

import com.example.musicapp.data.model.SongListData
import retrofit2.Response

class ApiServiceImpl : ApiService {

    override suspend fun getSongListFromServer(): Response<SongListData> {
        val retrofitBuilder = RetrofitHelper.getInstance()
        return retrofitBuilder.create(ApiService::class.java).getSongListFromServer()
    }

    override suspend fun getRecentlyPlayedSongListFromServer(): Response<SongListData> {
        val retrofitBuilder = RetrofitHelper.getInstance()
        return retrofitBuilder.create(ApiService::class.java).getRecentlyPlayedSongListFromServer()
    }

}