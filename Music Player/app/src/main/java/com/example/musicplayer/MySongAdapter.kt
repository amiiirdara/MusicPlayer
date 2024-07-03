package com.example.musicplayer

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import java.util.concurrent.TimeUnit


class MySongAdapter(var list: ArrayList<SongItem>, val listener: OnSongItemClickListener) :

    RecyclerView.Adapter<MySongAdapter.ViewHolder>(), RecyclerViewFastScroller.OnPopupTextUpdate {
    val selectedItem = ArrayList<SongItem>()
    var isSelectMode = false

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return ViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position], listener)

        //Set the songName and songDetail
        val item: SongItem = list.get(position)
        holder.txt_title.text = item.songName
        holder.txt_detail.text = item.songDetail

        //set the OnClickListener for each item
        holder.cns_root.setOnClickListener {
            listener.onItemClick(item, position)
        }


        //Set the cover of song
        if (item.songImageString!=null){
            Glide.with(holder.img_cover.context)
                .load(item.songImageString)
                .into(holder.img_cover)
        } else{
            Glide.with(holder.img_cover.context)
                .load(item.songImageUri)
                .into(holder.img_cover)

        }





        //Set the duration of song

        val songDuration = String.format(
            "%2d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(item.songDuration!!),
            TimeUnit.MILLISECONDS.toSeconds(item.songDuration!!) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(item.songDuration!!))
        )

        holder.txt_songTime.text = songDuration

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return list.size
    }

    //the class is holding the list view
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener {
        var isSelectMode = false
        var txt_title: TextView = itemView.findViewById(R.id.txt_SongName3)
        var txt_detail: TextView = itemView.findViewById(R.id.txt_detail3)
        var txt_songTime: TextView = itemView.findViewById(R.id.txt_songTime3)
        var cns_root: ConstraintLayout = itemView.findViewById(R.id.cns_root)
        var img_cover: ImageView = itemView.findViewById(R.id.img_songCover3)

        fun bindItems(user: SongItem, listener: OnSongItemClickListener) {
        }

        override fun onLongClick(v: View?): Boolean {
            return true
        }


    }

    fun setFilter(newList: ArrayList<SongItem>) {
        list = ArrayList()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onChange(position: Int): CharSequence {
        val header = list.get(position).songName?.first().toString()
        return header

    }




}