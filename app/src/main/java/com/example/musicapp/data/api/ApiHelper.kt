package com.example.musicapp.data.api

class ApiHelper(private val apiService: ApiService) {


    suspend fun getSongList() = apiService.getSongListFromServer();


    suspend fun getRecentlyPlayedSongList() = apiService.getSongListFromServer();

}