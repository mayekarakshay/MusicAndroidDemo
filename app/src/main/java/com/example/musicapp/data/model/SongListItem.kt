package com.example.musicapp.data.model


import com.google.gson.annotations.SerializedName

data class SongListItem (
    @SerializedName("album")
    val album: String = "",
    @SerializedName("artist")
    val artist: String= "",
    @SerializedName("image_url")
    val imageUrl: String= "",
    @SerializedName("link_url")
    val linkUrl: String= "",
    @SerializedName("name")
    val name: String= "",
    @SerializedName("played_at")
    val playedAt: String= "",
    @SerializedName("preview_url")
    val previewUrl: String= "",
    @SerializedName("sid")
    val sid: String= ""
)