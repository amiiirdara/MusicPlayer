package com.example.musicplayer.activity

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button

import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.MySongAdapter
import com.example.musicplayer.OnSongItemClickListener
import com.example.musicplayer.framework.PermissionHandler
import com.example.musicplayer.R
import com.example.musicplayer.SongItem
import com.example.musicplayer.framework.Debug
import com.example.musicplayer.framework.XActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.concurrent.TimeUnit


class MainActivity : XActivity() {

    lateinit var seekbar: SeekBar
    lateinit var btn_playHead: Button
    lateinit var btn_pauseHead: Button
    lateinit var btn_playSingle: Button
    lateinit var btn_pauseSingle: Button
    lateinit var btn_nextHead: Button
    lateinit var btn_nextSingle: Button
    lateinit var btn_previousHead: Button
    lateinit var btn_previousSingle: Button
    lateinit var btn_shuffle: Button
    lateinit var btn_repeat: Button
    lateinit var btn_repeatRed: Button

    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    var currentPlayingMusic: SongItem? = null
    var currentPlayingMusicIndex: Int = 0
    val mediaPlayer: MediaPlayer = MediaPlayer()
    var songItemsList = ArrayList<SongItem>()

    lateinit var songAdapter: MySongAdapter
    var isRepeatOff = true

    override

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleToolbar()
        handleSongRecyclerViewAndPlayingButtons()
        loadMusics()
        handleBottomSheet()
        handleSeekBar()
    }

    private fun handleSongRecyclerViewAndPlayingButtons() {

        val SongRecyclerView: RecyclerView = findViewById(R.id.songRecylerView)
        val img_songCoverHead = findViewById<ImageView>(R.id.img_songCoverHead)
        val txt_songNameHead = findViewById<TextView>(R.id.txt_songNameHead)
        val txt_songDetailHead = findViewById<TextView>(R.id.txt_songDetailHead)
        val img_songCoverSingle = findViewById<ImageView>(R.id.img_songCoverSingle)
        val txt_songNameSingle = findViewById<TextView>(R.id.txt_songNameSingle)
        val txt_songDetailSingle = findViewById<TextView>(R.id.txt_songDetailSingle)


        //handle the play and pause button for head and single mode
        btn_playHead = findViewById(R.id.btn_playHead)
        btn_pauseHead = findViewById(R.id.btn_pauseHead)
        btn_playHead.setOnClickListener({ playTheSong(true) })
        btn_pauseHead.setOnClickListener({ playTheSong(true) })
        //handle the play and pause button for head and single mode
        btn_playSingle = findViewById(R.id.btn_playSingle)
        btn_pauseSingle = findViewById(R.id.btn_pauseSingle)
        btn_playSingle.setOnClickListener({ playTheSong(true) })
        btn_pauseSingle.setOnClickListener({ playTheSong(true) })
        //play the next  and previous song and set next song cover and details on the head and the single mode
        btn_nextHead = findViewById(R.id.btn_nextHead)
        btn_nextHead.setOnClickListener({ playNextSong() })
        btn_nextSingle = findViewById(R.id.btn_nextSingle)
        btn_nextSingle.setOnClickListener({ playNextSong() })
        btn_previousHead = findViewById(R.id.btn_prevoiusHead)
        btn_previousHead.setOnClickListener({ playPreviousSong() })
        btn_previousSingle = findViewById(R.id.btn_previousSingle)
        btn_previousSingle.setOnClickListener({ playPreviousSong() })
        //play the shuffle music
        btn_shuffle = findViewById(R.id.btn_shuffle)
        btn_shuffle.setOnClickListener({ Shuffle() })
        //handle repeat button
        btn_repeat = findViewById(R.id.btn_repeatSingle)
        btn_repeat.setOnClickListener({ repeat() })
        btn_repeatRed = findViewById(R.id.btn_repeatSingleRed)
        btn_repeatRed.setOnClickListener({ repeat() })
        //set on click listener for song cover to scroll smoothly to song when clicked
        img_songCoverHead.setOnClickListener {

            SongRecyclerView.scrollToPosition(currentPlayingMusicIndex)
            Handler().postDelayed({
                val anim = AlphaAnimation(0.0f, 1.0f)
                anim.duration = 500
                anim.fillAfter = true
                SongRecyclerView.findViewHolderForAdapterPosition(currentPlayingMusicIndex)?.itemView?.startAnimation(
                    anim
                )

            }, 500)

        }


        //on recycler view item click listener
        songAdapter = MySongAdapter(songItemsList, object : OnSongItemClickListener {
            override fun onItemClick(item: SongItem?, position: Int) {

                currentPlayingMusic = item
                currentPlayingMusicIndex = position

                // imp *** here we can't just call function setSongItems() because it is not working fine when we use searchView to find our song
                txt_songNameHead.text = item?.songName
                txt_songDetailHead.text = item?.songDetail
                img_songCoverHead.setImageURI(item?.songImageUri)
                txt_songNameSingle.text = item?.songName
                txt_songDetailSingle.text = item?.songDetail
                img_songCoverSingle.setImageURI(item?.songImageUri)

                currentPlayingMusic =
                    item                                           //imp ***  setting the clicked music as current playing music
                playTheSong(false)                                       // playing the clicked song suddenly after clicking
                currentPlayingMusicIndex = position
            }
        })

        SongRecyclerView.adapter = songAdapter
        SongRecyclerView.layoutManager = LinearLayoutManager(this)
        songAdapter.notifyDataSetChanged()
    }

    @SuppressLint("Range")
    fun loadMusics() {
        val resolver: ContentResolver = contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = resolver.query(uri, null, null, null, null)
        when {
            cursor == null -> {
                // query failed, handle error.
            }

            !cursor.moveToFirst() -> {
                Toast.makeText(this, "No Audio is available on this device", Toast.LENGTH_SHORT)
                    .show()
                // no media on the device
            }

            else -> {

                do {
                    val songURI =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songAuthor =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val songDuration =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumID =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                    if (songDuration != null) {


                        songItemsList.add(
                            SongItem(
                                songDuration.toLong(),
                                songName,
                                songAuthor,
                                songURI,
                                getSongCover(albumID)
                            )

                        )
                    }


                } while (cursor.moveToNext())
            }
        }
        cursor?.close()

        //sort list view items by alphabet order
        songItemsList.sortBy { it.songName?.lowercase() }
    }


    private fun getSongCover(albumID: String): Uri? {
        try {
            val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
            val uri = ContentUris.withAppendedId(sArtworkUri, albumID.toLong())
            return uri
        } catch (e: Exception) {
            return null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        val inflater: MenuInflater = menuInflater;
        inflater.inflate(R.menu.menu_actionbar, menu)
        val searchView = (menu!!.findItem(R.id.menu_searchView).actionView as SearchView?)!!
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val myNewText = newText!!.lowercase()
                val newList: ArrayList<SongItem> = ArrayList()
                for (songItem: SongItem in songItemsList) {
                    val songName: String = songItem.songName!!.lowercase()
                    if (songName.contains(myNewText)) {
                        newList.add(songItem)
                    }
                }

                songAdapter.setFilter(newList)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


    private fun handleToolbar() {
        toolbar = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar)
    }

    private fun handleBottomSheet() {

        val sheetLayout = findViewById<ConstraintLayout>(R.id.sheetCollapsed)
        val bottomSheet = BottomSheetBehavior.from(sheetLayout)
        val headSheet = findViewById<ConstraintLayout>(R.id.cns_headSheet)

        val backgroundArray = IntArray(9)
        backgroundArray[0] = R.drawable.bg_gradient1
        backgroundArray[1] = R.drawable.bg_gradient2
        backgroundArray[2] = R.drawable.bg_gradient3
        backgroundArray[3] = R.drawable.bg_gradient4
        backgroundArray[4] = R.drawable.bg_gradient5
        backgroundArray[5] = R.drawable.bg_gradient6
        backgroundArray[6] = R.drawable.bg_gradient7
        backgroundArray[7] = R.drawable.bg_gradient8
        backgroundArray[8] = R.drawable.bg_gradient9

        bottomSheet.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        sheetLayout.setBackgroundResource(backgroundArray.get((Math.random() * 9).toInt()))
                        headSheet.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        headSheet.visibility = View.INVISIBLE
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    //BottomSheetBehavior.STATE_SETTLING -> Toast.makeText(this@MainActivity, "STATE_SETTLING", Toast.LENGTH_SHORT).show()
                    //BottomSheetBehavior.STATE_HIDDEN -> Toast.makeText(this@MainActivity, "STATE_HIDDEN", Toast.LENGTH_SHORT).show()
                    // else -> Toast.makeText(this@MainActivity, "OTHER_STATE", Toast.LENGTH_SHORT).show()

                }
            }


        })

    }

    private fun playTheSong(isPlayButtonClicked: Boolean) {

        if (isPlayButtonClicked) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                btn_playHead.visibility = View.VISIBLE
                btn_playSingle.visibility = View.VISIBLE

                btn_pauseHead.visibility = View.INVISIBLE
                btn_pauseSingle.visibility = View.INVISIBLE
                return
            } else {
                mediaPlayer.start()
                btn_playHead.visibility = View.INVISIBLE
                btn_pauseHead.visibility = View.VISIBLE
                btn_playSingle.visibility = View.INVISIBLE
                btn_pauseSingle.visibility = View.VISIBLE
                return
            }
        } else {
            if (currentPlayingMusic?.songUrl != null) {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(currentPlayingMusic!!.songUrl)
                mediaPlayer.prepare()
                mediaPlayer.start()
                btn_playHead.visibility = View.INVISIBLE
                btn_pauseHead.visibility = View.VISIBLE
                btn_playSingle.visibility = View.INVISIBLE
                btn_pauseSingle.visibility = View.VISIBLE

            }
        }
    }

    private fun handleSeekBar() {

        seekbar = findViewById(R.id.seekbar)
        val txt_passedTime: TextView = findViewById(R.id.txt_passedTime)
        val txt_songTime: TextView = findViewById(R.id.txt_songTime)

        mediaPlayer.setOnPreparedListener {

            val songTime = String.format(
                "%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(currentPlayingMusic?.songDuration!!),
                TimeUnit.MILLISECONDS.toSeconds(currentPlayingMusic?.songDuration!!) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(currentPlayingMusic?.songDuration!!)
                )
            )
            txt_songTime.text = songTime



            seekbar.max = currentPlayingMusic!!.songDuration!!.toInt() / 500


            val mHandler = Handler()
            this@MainActivity.runOnUiThread(object : Runnable {
                override fun run() {
                    if (mediaPlayer != null) {
                        val CurrentTimePosition: Int = mediaPlayer.getCurrentPosition() / 500
                        seekbar.setProgress(CurrentTimePosition)

                    }
                    mHandler.postDelayed(this, 500)
                }
            })
        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {

                    mediaPlayer.seekTo(progress * 500)
                }

                val songTimePassed = String.format(
                    "%2d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.currentPosition.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.currentPosition.toLong()) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.currentPosition.toLong())
                    )
                )
                txt_passedTime.text = songTimePassed
            }
        })
    }

    private fun playNextSong() {

        if (currentPlayingMusicIndex < songItemsList.size - 1) {
            currentPlayingMusic = songItemsList.get(currentPlayingMusicIndex + 1)
            currentPlayingMusicIndex++
            setSongItems()
            playTheSong(false)
        } else {
            currentPlayingMusicIndex = 0
            currentPlayingMusic = songItemsList.get(0)
            setSongItems()
            playTheSong(false)
        }

    }

    private fun playPreviousSong() {

        if (currentPlayingMusicIndex > 0) {
            currentPlayingMusic = songItemsList.get(currentPlayingMusicIndex - 1)
            currentPlayingMusicIndex--
            setSongItems()
            playTheSong(false)
        } else {
            currentPlayingMusicIndex = 0
            currentPlayingMusic = songItemsList.get(0)
            setSongItems()
            playTheSong(false)
        }
    }

    private fun Shuffle() {

        if (currentPlayingMusic != null) {
            currentPlayingMusicIndex = (Math.random() * (songItemsList.size - 1)).toInt()
            currentPlayingMusic = songItemsList.get(currentPlayingMusicIndex)
            playTheSong(false)
            setSongItems()
        }

    }

    private fun repeat() {
        if (isRepeatOff) {
            btn_repeat.visibility = View.INVISIBLE
            btn_repeatRed.visibility = View.VISIBLE
            mediaPlayer.setOnCompletionListener {
                setSongItems()
                playTheSong(false)
            }
            isRepeatOff = false
            return
        } else {
            btn_repeatRed.visibility = View.INVISIBLE
            btn_repeat.visibility = View.VISIBLE
            isRepeatOff = true
            mediaPlayer.setOnCompletionListener {
                if (currentPlayingMusicIndex < songItemsList.size - 1) {
                    currentPlayingMusic = songItemsList.get(currentPlayingMusicIndex + 1)
                    currentPlayingMusicIndex = currentPlayingMusicIndex + 1
                    playTheSong(false)
                    setSongItems()
                }

            }
            return
        }
    }

    private fun setSongItems() {
        val img_songCoverHead = findViewById<ImageView>(R.id.img_songCoverHead)
        val txt_songNameHead = findViewById<TextView>(R.id.txt_songNameHead)
        val txt_songDetailHead = findViewById<TextView>(R.id.txt_songDetailHead)
        val img_songCoverSingle = findViewById<ImageView>(R.id.img_songCoverSingle)
        val txt_songNameSingle = findViewById<TextView>(R.id.txt_songNameSingle)
        val txt_songDetailSingle = findViewById<TextView>(R.id.txt_songDetailSingle)
        txt_songNameHead.text = songItemsList[currentPlayingMusicIndex].songName
        txt_songDetailHead.text = songItemsList[currentPlayingMusicIndex].songDetail
        img_songCoverHead.setImageURI(songItemsList[currentPlayingMusicIndex].songImageUri)
        txt_songNameSingle.text = songItemsList[currentPlayingMusicIndex].songName
        txt_songDetailSingle.text = songItemsList[currentPlayingMusicIndex].songDetail
        img_songCoverSingle.setImageURI(songItemsList[currentPlayingMusicIndex].songImageUri)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.menu_online){
            val intent = Intent(this@MainActivity,OnlinePlaylistActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }




    override fun onBackPressed() {
        if (songAdapter.isSelectMode) {
            songAdapter.selectedItem.clear()
            songAdapter.isSelectMode = false
            return
        }

        super.onBackPressed()
    }

}

