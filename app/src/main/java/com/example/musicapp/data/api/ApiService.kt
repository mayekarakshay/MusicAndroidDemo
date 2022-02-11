package com.example.musicapp.data.api

import com.example.musicapp.data.model.SongListData
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("/nowplaying/v3/935/testapi")
    suspend fun getSongListFromServer(): Response<SongListData>


    @GET("/nowplaying/v3/935/testapi")
    suspend fun getRecentlyPlayedSongListFromServer(): Response<SongListData>

}