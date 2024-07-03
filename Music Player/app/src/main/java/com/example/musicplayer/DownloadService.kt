package com.example.musicplayer

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadService {
    @Streaming // Important for downloading large files
    @GET
    suspend fun downloadAudio(@Url fileUrl: String): ResponseBody
}