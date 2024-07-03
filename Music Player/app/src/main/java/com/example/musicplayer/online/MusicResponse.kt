package com.example.musicplayer.online

import com.google.gson.annotations.SerializedName

class MusicResponse {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("detail")
    var detail: String = ""

    @SerializedName("duration")
    var duration: Long = 0

    @SerializedName("url")
    var url: String = ""

    @SerializedName("imageUrl")
    var imageUrl: String = ""

}