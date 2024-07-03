package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.activity.MainActivity
import com.example.musicplayer.framework.MToast
import java.util.concurrent.TimeUnit


class OnlineSongAdapter(var list: ArrayList<SongItem>) :
    RecyclerView.Adapter<OnlineSongAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.single_item_online, parent, false)
        return ViewHolder(v)
    }


    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Set the songName and songDetail
        val item: SongItem = list[position]
        holder.txt_title.text = item.songName
        holder.txt_detail.text = item.songDetail


        //Set the cover of song
        Glide.with(holder.img_cover.context).load(item.songImageString).into(holder.img_cover)

        //Set the duration of song

        val songDuration = String.format(
            "%2d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(item.songDuration!!),
            TimeUnit.MILLISECONDS.toSeconds(item.songDuration!!) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(item.songDuration!!))
        )

        holder.txt_songTime.text = songDuration

        holder.btnAdd.setOnClickListener {
            MToast("Download Started!")
            downloadFile(holder.img_cover.context, item.songUrl!!, "${item.songName}.mp3")
        }

    }


    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return list.size
    }

    //the class is holding the list view
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_title: TextView = itemView.findViewById(R.id.txt_onlineName)
        var txt_detail: TextView = itemView.findViewById(R.id.txt_onlineDetail)
        var txt_songTime: TextView = itemView.findViewById(R.id.txt_onlineDuration)
        var img_cover: ImageView = itemView.findViewById(R.id.onlineSongCover)
        var btnAdd: Button = itemView.findViewById(R.id.btn_onlineAdd)
    }

    private fun downloadFile(context: Context, url: String, fileName: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)

        // Set download destination and file name
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        // Set notification visibility
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        // Set title and description for the notification
        request.setTitle(fileName)
        request.setDescription("Downloading...")

        // Enqueue the download request
        downloadManager.enqueue(request)
    }


}