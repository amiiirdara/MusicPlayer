package com.example.musicplayer

import android.graphics.Bitmap
import android.net.Uri

class SongItem {

    var songDuration: Long?
    var songName: String? = "Unknown"
    var songDetail: String? = "Unknown"
    var songUrl: String? = null
    var songImageInt: Int  = R.drawable.artistic_album_cover_design_template_d12ef0296af80b58363dc0deef077ecc_screen
    var songImageBitmap:Bitmap? = null
    var songImageUri:Uri? = null
    var songImageString:String? = null

    constructor(songDuration: Long, songName: String, songDetail: String, songUrl: String, songImage : Int) {

        this.songDuration = songDuration
        this.songName =songName
        this.songDetail =songDetail
        this.songUrl =songUrl
        this.songImageInt = songImage
    }
    constructor(songDuration:Long, songName: String, songDetail: String, songUrl: String , songImage : Bitmap?) {

        this.songDuration = songDuration
        this.songName =songName
        this.songDetail =songDetail
        this.songUrl =songUrl
        this.songImageBitmap = songImage
    }

    constructor(songDuration:Long, songName: String, songDetail: String, songUrl: String, songImage: Uri?) {
        this.songDuration = songDuration
        this.songName =songName
        this.songDetail =songDetail
        this.songUrl =songUrl
        this.songImageUri = songImage
    }

    constructor(songDuration:Long, songName: String, songDetail: String, songUrl: String, songImage: String?) {
        this.songDuration = songDuration
        this.songName =songName
        this.songDetail =songDetail
        this.songUrl =songUrl
        this.songImageString = songImage
    }

}