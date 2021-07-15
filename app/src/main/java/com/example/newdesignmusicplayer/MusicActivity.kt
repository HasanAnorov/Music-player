package com.example.newdesignmusicplayer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.example.newdesignmusicplayer.Services.OnClearFromRecentService
import com.example.newdesignmusicplayer.databinding.ActivityMusicNewBinding
import com.example.newdesignmusicplayer.model.ModelAudio
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.Serializable
import java.text.FieldPosition

open class MusicActivity : AppCompatActivity(),Serializable {

    private lateinit var binding:ActivityMusicNewBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var audioArrayList: ArrayList<ModelAudio>
    var current_pos = 0.0
    private var total_duration: Double = 0.0
    private var audio_index = 0
    private var notificationManager: NotificationManager? = null


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.cardBookmark.elevation = 0F
        binding.cardAddToList.elevation = 0F
        binding.cardRepeat.elevation = 0F
        binding.cardShuffle.elevation = 0F

        //changing status and navigationBar colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = getColor(R.color.musicActivity)
            window.navigationBarColor = getColor(R.color.musicActivity)
        }

        val position = intent.getIntExtra("pos", 0)
        audioArrayList = intent.getSerializableExtra("musics") as ArrayList<ModelAudio>

        checkPermissions()
        setAudio(position,audioArrayList)

        val broadcastReceiver = object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.getStringExtra("actionname")
                when(action){
                    CreateNotification().ACTION_NEXT -> {
                        //Toast.makeText(context, "Notification Next", Toast.LENGTH_SHORT).show()
                        nextAudio(audioArrayList)
                    }
                    CreateNotification().ACTION_PLAY ->{
                        //Toast.makeText(context, "Notification Play", Toast.LENGTH_SHORT).show()
                        setPause(audioArrayList)
                    }
                    CreateNotification().ACTION_PREVIOUS ->{
                        prevAudio(audioArrayList)
                        //Toast.makeText(context, "Notification Previous", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //creating notification channel
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            createChannel()
            registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
            startService(Intent(baseContext, OnClearFromRecentService::class.java))
        }

        binding.playNext.setOnClickListener {
            nextAudio(audioArrayList)
        }
        binding.playPrevious.setOnClickListener {
            prevAudio(audioArrayList)
        }
        binding.btnPlayPause.setOnClickListener {
            setPause(audioArrayList)
        }
        binding.btnArrow.setOnClickListener {
            onBackPressed()
        }
    }

    //creating channel
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CreateNotification().CHANNEL_ID,"AppNameHasan",NotificationManager.IMPORTANCE_LOW).apply {
                setShowBadge(false)
            }
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    //getting audio image
    fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }

    //setting audio files
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setAudio(pos: Int,audioArrayList:ArrayList<ModelAudio>) {

        var isRepeatActivated = false
        var isRandomPlayingActivated = false
        var isFavorite = true

        audio_index = pos

        mediaPlayer = MediaPlayer()
        mediaPlayer.reset()
        //mediaPlayer.playbackParams.speed
        mediaPlayer.apply {
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setAudioAttributes(
                    AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            )
        }

        binding.cardShuffle.setOnClickListener {

            isRandomPlayingActivated =!isRandomPlayingActivated

            var cardDrawable: Drawable = binding.cardShuffle.background
            cardDrawable = DrawableCompat.wrap(cardDrawable)
            var ivDrawable = binding.shuffleIv.background
            ivDrawable = DrawableCompat.wrap(ivDrawable)

            if(isRandomPlayingActivated) {

                DrawableCompat.setTint(cardDrawable, resources.getColor(R.color.shuffleColor))
                binding.cardShuffle.background = cardDrawable

                DrawableCompat.setTint(ivDrawable, resources.getColor(R.color.white))
                binding.shuffleIv.background = ivDrawable
            }

            else{
                DrawableCompat.setTint(cardDrawable, resources.getColor(R.color.musicActivity))
                binding.cardShuffle.background = cardDrawable

                DrawableCompat.setTint(ivDrawable, resources.getColor(R.color.shuffleColor))
                binding.shuffleIv.background = ivDrawable
            }

            Toast.makeText(this, "shuffle", Toast.LENGTH_SHORT).show()
            if (isRepeatActivated){
                isRepeatActivated = false
            }
        }

        binding.cardBookmark.setOnClickListener {

            isFavorite = !isFavorite

            var cardDrawable: Drawable = binding.cardBookmark.background
            cardDrawable = DrawableCompat.wrap(cardDrawable)

            var ivDrawable = binding.bookmarkIv.background
            ivDrawable = DrawableCompat.wrap(ivDrawable)

            if(isFavorite){
                DrawableCompat.setTint(cardDrawable, resources.getColor(R.color.shuffleColor))
                binding.cardBookmark.background = cardDrawable

                binding.bookmarkIv.setImageResource(R.drawable.ic_heart)
            }else{
                DrawableCompat.setTint(cardDrawable, resources.getColor(R.color.musicActivity))
                binding.cardBookmark.background = cardDrawable

                binding.bookmarkIv.setImageResource(R.drawable.ic_heart__6_)
            }

            Toast.makeText(this, "bookmark", Toast.LENGTH_SHORT).show()
        }

        binding.cardRepeat.setOnClickListener{

            isRepeatActivated = !isRepeatActivated

            var cardDrawable: Drawable = binding.cardRepeat.background
            cardDrawable = DrawableCompat.wrap(cardDrawable)

            var ivDrawable = binding.repeatIv.background
            ivDrawable = DrawableCompat.wrap(ivDrawable)

            if(isRepeatActivated){
                DrawableCompat.setTint(cardDrawable, resources.getColor(R.color.shuffleColor))
                binding.cardRepeat.background = cardDrawable

                DrawableCompat.setTint(ivDrawable, resources.getColor(R.color.white))
                binding.repeatIv.background = ivDrawable
            }else{
                DrawableCompat.setTint(cardDrawable, resources.getColor(R.color.musicActivity))
                binding.cardRepeat.background = cardDrawable

                DrawableCompat.setTint(ivDrawable, resources.getColor(R.color.shuffleColor))
                binding.repeatIv.background = ivDrawable
            }

            Toast.makeText(this, "repeat", Toast.LENGTH_SHORT).show()

            if (isRandomPlayingActivated){
                isRandomPlayingActivated = false
            }
        }

        binding.cardAddToList.setOnClickListener {
            Toast.makeText(this, "addToList", Toast.LENGTH_SHORT).show()
        }


        playAudio(pos,audioArrayList)

        //seekbar change listener
        binding.tvSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                current_pos = seekBar!!.progress.toDouble()
                mediaPlayer.seekTo(current_pos.toInt())
            }
        })

        mediaPlayer.setOnCompletionListener {
            if (isRandomPlayingActivated){
                val random = (0 until audioArrayList.size).random()
                audio_index = random
            }
            if (!isRandomPlayingActivated && !isRepeatActivated){
                audio_index++
            }
            if (audio_index < (audioArrayList.size)) {
                playAudio(audio_index,audioArrayList)
            } else {
                audio_index = 0
                playAudio(audio_index,audioArrayList)
            }
        }
    }

    //play audio file
    private fun playAudio(pos: Int,audioArrayList: ArrayList<ModelAudio>) {
        try {
            val image = audioArrayList[pos].audioUri?.let { getAlbumArt(it) }
            if (image!=null){
                Glide.with(this).asBitmap().load(image).into(binding.musicPhoto)
            }
            binding.playPause.setImageResource(R.drawable.ic_pause)
            binding.musicName.text = audioArrayList[pos].audioTitle
            binding.musicAuthor.text = audioArrayList[pos].audioArtist
            binding.musicDuration.text = audioArrayList[pos].audioDuration

            mediaPlayer.stop()
            mediaPlayer.reset()

            //set file path
            CreateNotification().createNotification(this,audioArrayList[pos],R.drawable.ic_pause,timerConversion(mediaPlayer.currentPosition.toLong()).toLong())
            mediaPlayer.setDataSource(applicationContext, Uri.parse(audioArrayList[pos].audioUri!!))
            mediaPlayer.prepare()
            mediaPlayer.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        setAudioProgress()
    }

    //set audio progress
    private fun setAudioProgress() {
        //get the audio duration
        current_pos = mediaPlayer.currentPosition.toDouble()
        total_duration = mediaPlayer.duration.toDouble()

        //display the audio duration
        binding.musicDuration.text = timerConversion(total_duration.toLong())
        binding.tvStartTime.text = timerConversion(current_pos.toLong())
        binding.tvSeekBar.max = total_duration.toInt()
        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            override fun run() {
                try {
                    current_pos = mediaPlayer.currentPosition.toDouble()
                    binding.tvStartTime.text = timerConversion(current_pos.toLong())
                    binding.tvSeekBar.progress = current_pos.toInt()
                    handler.postDelayed(this, 1000)
                } catch (ed: IllegalStateException) {
                    ed.printStackTrace()
                }
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    //play previous audio
    private fun prevAudio(audioArrayList:ArrayList<ModelAudio> ) {
        if (audio_index > 0) {
            audio_index--
            playAudio(audio_index,audioArrayList)
           // CreateNotification().createNotification(this,audioArrayList[audio_index],R.drawable.ic_pause)
        } else {
            audio_index = audioArrayList.size - 1
            playAudio(audio_index,audioArrayList)
            //CreateNotification().createNotification(this,audioArrayList[audio_index],R.drawable.ic_pause)
        }
    }

    //play next audio
    private fun nextAudio(audioArrayList:ArrayList<ModelAudio> ) {
        if (audio_index < audioArrayList.size - 1) {
            audio_index++
            playAudio(audio_index,audioArrayList)
            //CreateNotification().createNotification(this,audioArrayList[audio_index],R.drawable.ic_pause)
        } else {
            audio_index = 0
            playAudio(audio_index,audioArrayList)
            //CreateNotification().createNotification(this,audioArrayList[audio_index],R.drawable.ic_pause)
        }
    }

    //pause audio
    private fun setPause(audioArrayList: ArrayList<ModelAudio>) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            binding.playPause.setImageResource(R.drawable.ic_play_button_arrowhead)
            CreateNotification().createNotification(this,audioArrayList[audio_index],R.drawable.ic_play_button_arrowhead,timerConversion(mediaPlayer.currentPosition.toLong()).toLong())
            //Toast.makeText(this, audioArrayList[audio_index].audioDuration, Toast.LENGTH_SHORT).show()
        } else {
            mediaPlayer.start()
            binding.playPause.setImageResource(R.drawable.ic_pause)
            CreateNotification().createNotification(this,audioArrayList[audio_index],R.drawable.ic_pause,timerConversion(mediaPlayer.currentPosition.toLong()).toLong())
        }
    }

    //time conversion
    fun timerConversion(value: Long): String {
        val audioTime: String
        val dur = value.toInt()
        val hrs = dur / 3600000
        val mns = dur / 60000 % 60000
        val scs = dur % 60000 / 1000
        audioTime = if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mns, scs)
        } else {
            String.format("%02d:%02d", mns, scs)
        }
        return audioTime
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    //release mediaplayer
    override fun onDestroy() {
        super.onDestroy()

    }

    //checking permission
    private fun checkPermissions() {
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                    override fun onPermissionRationaleShouldBeShown(
                            permissionRequest: PermissionRequest,
                            permissionToken: PermissionToken
                    ) {
                        // asking for permission
                        permissionToken.continuePermissionRequest()
                    }
                }).check()
    }

}
