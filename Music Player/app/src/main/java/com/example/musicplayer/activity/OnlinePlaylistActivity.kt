package com.example.musicplayer.activity

import android.annotation.SuppressLint

import android.os.Bundle
import android.view.View

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.musicplayer.MySongAdapter
import com.example.musicplayer.OnSongItemClickListener
import com.example.musicplayer.OnlineSongAdapter
import com.example.musicplayer.R
import com.example.musicplayer.SongItem
import com.example.musicplayer.framework.Debug
import com.example.musicplayer.framework.MToast
import com.example.musicplayer.framework.XActivity
import com.example.musicplayer.online.API
import com.example.musicplayer.online.ApiInterface
import com.example.musicplayer.online.MusicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnlinePlaylistActivity : XActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    var songItemsList = ArrayList<SongItem>()

    lateinit var songAdapter: OnlineSongAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_playlist)
        handleToolbar()
        loadMusicsFromServer()

    }

    private fun handleToolbar() {
        toolbar = findViewById(R.id.toolbarOnline)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "New Musics"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    @SuppressLint("Range")
    private fun loadMusicsFromServer() {

        val progressBar = findViewById<View>(R.id.progressBarOnline)
        val rootConstraintlayout = findViewById<ConstraintLayout>(R.id.constraintLayoutOnline)


        val apiInterface = API.getApi().create(ApiInterface::class.java)
        val musicsCall: Call<ArrayList<MusicResponse>> = apiInterface.musicsCall


        musicsCall.enqueue(object : Callback<ArrayList<MusicResponse>> {
            override fun onResponse(
                call: Call<ArrayList<MusicResponse>>, response: Response<ArrayList<MusicResponse>>
            ) {
                if (response.body() != null) {
                    progressBar.visibility = View.INVISIBLE
                    rootConstraintlayout.alpha = 1f

                    for (item in response.body()!!) {
                        Debug.logInfo("Music Added!")
                        songItemsList.add(
                            SongItem(
                                item.duration, item.name, item.detail, item.url, item.imageUrl
                            )
                        )
                    }

                    handleRecyclerView()
                }

            }

            override fun onFailure(call: Call<ArrayList<MusicResponse>>, t: Throwable) {
                progressBar.visibility = View.INVISIBLE
                rootConstraintlayout.alpha = 1f
                Debug.logInfo("Music Call  : " + t.message.toString())
                MToast("check your connection!")
            }

        })
    }

    private fun handleRecyclerView() {

        val recyclerView =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewOnline)
        songAdapter = OnlineSongAdapter(songItemsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = songAdapter
        songAdapter.notifyDataSetChanged()
    }

}