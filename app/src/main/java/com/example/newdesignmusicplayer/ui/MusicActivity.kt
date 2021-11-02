package com.example.newdesignmusicplayer.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.*
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.newdesignmusicplayer.R
import com.example.newdesignmusicplayer.databinding.ActivityMusicNewBinding
import com.example.newdesignmusicplayer.room.RoomAudioModel
import com.example.newdesignmusicplayer.services.MediaService
import com.example.newdesignmusicplayer.services.OnClearFromRecentService
import com.example.newdesignmusicplayer.utils.CreateNotification
import com.example.newdesignmusicplayer.viewmodel.MediaViewModel
import java.io.Serializable
import android.os.Bundle
import android.content.Intent
import android.content.BroadcastReceiver
import androidx.localbroadcastmanager.content.LocalBroadcastManager

open class MusicActivity : AppCompatActivity(),Serializable {

    private lateinit var binding:ActivityMusicNewBinding
    lateinit var audioArrayList: List<RoomAudioModel>
    var current_pos = 0.0
    private var total_duration: Double = 0.0
    private var audio_index = 0
    private var notificationManager: NotificationManager? = null
    private lateinit var viewModel:MediaViewModel
    private var musicService  : MediaService ? = MediaService()
    val TAG = "MUSIC_ACTIVITY"
    private var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MediaService.LocalBinder
            musicService = binder.getService()

            Log.d(TAG,"onServiceConnected getting musicService instance - $musicService")
            mBound = true
            Log.d(TAG, "onServiceConnection and getting duration - ${musicService!!.mediaPlayer}")
            Log.d(TAG, "onServiceConnection and getting duration1 - ${musicService!!.mediaPlayer?.duration}")
            Log.d(TAG, "onServiceConnection and getting duration2 - ${musicService!!.mediaPlayer?.isPlaying}")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG,"onServiceDisconnect")
            mBound = false
        }
    }


    override fun onStart() {
        super.onStart()
        Intent(this, MediaService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            Log.d(TAG,"onStart")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate")
        binding = ActivityMusicNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        viewModel = ViewModelProvider(this).get(MediaViewModel::class.java)

        //this relates to mMessageReceiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter("MediaPlayerDurationUpdates"))

        binding.cardBookmark.elevation = 0F
        binding.cardAddToList.elevation = 0F
        binding.cardRepeat.elevation = 0F
        binding.cardShuffle.elevation = 0F

        //changing status and navigationBar colors
        window.statusBarColor = getColor(R.color.musicActivity)
        window.navigationBarColor = getColor(R.color.musicActivity)

        val position = intent.getIntExtra("position", 0)
        val folderName = intent.getStringExtra("folderName") as String

        Log.d(TAG, "call to service")
        // start localService
        Intent(this, MediaService::class.java).also { intent ->
            intent.putExtra("service_position",position)
            intent.putExtra("folder_name",folderName)
            startService(intent)
        }

        viewModel.getFolder(folderName).observe(this){
            audioArrayList = it.audioList
            setAudio(position, audioArrayList)
            Log.d(TAG,audioArrayList[position].audioTitle + " ------ " + audioArrayList[position].audioDuration )
        }

        val broadcastReceiver = object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.getStringExtra("actionname")
                when(action){
                    CreateNotification().ACTION_NEXT -> {
                        nextAudio(audioArrayList)
                    }
                    CreateNotification().ACTION_PLAY -> {
                        setPause(audioArrayList)
                        Log.d(TAG,"action play in notification")
                    }
                    CreateNotification().ACTION_PREVIOUS -> {
                        prevAudio(audioArrayList)
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

    //you can send data to activity if you want
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("mediaDuration")
            Log.d(TAG,"hello receiver and message  -  $message")
            binding.musicDuration.text = timerConversion(message.toString().toLong())
            total_duration = message.toString().toDouble()

        }
    }

    //setting audio files
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setAudio(pos: Int, audioArrayList: List<RoomAudioModel>) {
        Log.d(TAG, "setAudio -  $musicService")

        var isRepeatActivated = false
        var isRandomPlayingActivated = false

       // audio_index = pos

        viewModel.getMusic(pos + 1).observe(this){ roomAudioModel ->
            var cardDrawableBookmark: Drawable = binding.cardBookmark.background
            cardDrawableBookmark = DrawableCompat.wrap(cardDrawableBookmark)

            val state = roomAudioModel.isFavorite
            var stateBoolean: Boolean

            if(state==1){
                stateBoolean = true
                binding.bookmarkIv.setImageResource(R.drawable.ic_heart)
            }else{
                stateBoolean = false
                DrawableCompat.setTint(cardDrawableBookmark, resources.getColor(R.color.musicActivity))
                binding.cardBookmark.background = cardDrawableBookmark
                binding.bookmarkIv.setImageResource(R.drawable.ic_heart__6_)
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
                stateBoolean = !stateBoolean
                viewModel.getFolder("Favorites").observe(this){ roomFolderModel ->

                    if (stateBoolean){
                        viewModel.setFavorite(1, audioArrayList[pos].audioTitle)
                        roomFolderModel.audioList.toMutableList().add(audioArrayList[pos])
                        val folderList = ArrayList<RoomAudioModel>()
                        folderList.addAll(roomFolderModel.audioList)
                        folderList.add(audioArrayList[pos])
                        roomFolderModel.audioList = folderList
                        viewModel.updateFolder(roomFolderModel)
                        binding.bookmarkIv.setImageResource(R.drawable.ic_heart)
                    }else{
                        viewModel.setFavorite(0, audioArrayList[pos].audioTitle)
                        val folderList = ArrayList<RoomAudioModel>()
                        folderList.addAll(roomFolderModel.audioList)
                        folderList.remove(audioArrayList[pos])
                        roomFolderModel.audioList = folderList
                        viewModel.updateFolder(roomFolderModel)
                        binding.cardBookmark.background = cardDrawableBookmark
                        binding.bookmarkIv.setImageResource(R.drawable.ic_heart__6_)
                    }
                }
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
                val intent =Intent(this, FolderSelectionActivity::class.java)
                intent.putExtra("data", listOf(audioArrayList[pos]) as Serializable)
                startActivity(intent)
            }

        }

        //playAudio(pos)

        //seekbar change listener
        binding.tvSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                current_pos = seekBar!!.progress.toDouble()
                musicService!!.seekTo(current_pos.toInt())
            }
        })

        //musicService.setOnCompleteListener(isRandomPlayingActivated, isRepeatActivated)
        //musicService.onCompleted()
    }

    //play audio file
    private fun playAudio(pos: Int) {

        //Playing music from service
        Log.d(TAG, "playAudio - $musicService")
        Log.d(TAG, "playAudio mediaPlayer 001- ${musicService!!.mediaPlayer}")
        Log.d(TAG, "playAudio mediaPlayer 002- ${musicService!!.mediaPlayer?.duration}")

        Log.d(TAG,"given position - " + pos)

            //musicService!!.playMedia(pos)

            //Log.d(TAG, "playAudio - " + musicService.getDuration())
            val image = getAlbumArt(audioArrayList[pos].audioUri)

            if (image!=null){
                Glide.with(applicationContext).asBitmap().load(image).into(binding.musicPhoto)
            }
            binding.playPause.setImageResource(R.drawable.ic_pause)
            binding.musicName.text = audioArrayList[pos].audioTitle
            binding.musicAuthor.text = audioArrayList[pos].audioArtist

            //set file path
            CreateNotification().createNotification(this, audioArrayList[pos], R.drawable.ic_pause)

        //setAudioProgress()
    }

    //creating channel
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CreateNotification().CHANNEL_ID, "AppNameHasan", NotificationManager.IMPORTANCE_LOW).apply {
                setShowBadge(false)
            }
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    //getting audio image
    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }


    //set audio progress
    private fun setAudioProgress() {

        //get the audio duration
        current_pos = musicService!!.currentPosition()!!
        total_duration = musicService!!.duration()!!
        Log.d(TAG, "setAudioProgress - $current_pos " +
                "totalDuration -   $total_duration")

        //display the audio duration
        binding.musicDuration.text = timerConversion(total_duration.toLong())
        binding.tvStartTime.text = timerConversion(current_pos.toLong())
        binding.tvSeekBar.max = total_duration.toInt()
        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            override fun run() {
                try {
                    current_pos = musicService!!.currentPosition()!!.toDouble()
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
    private fun prevAudio(audioArrayList: List<RoomAudioModel>) {
        if (audio_index > 0) {
            audio_index--
            playAudio(audio_index)
        } else {
            audio_index = audioArrayList.size - 1
            playAudio(audio_index)
        }
    }

    //play next audio
    private fun nextAudio(audioArrayList: List<RoomAudioModel>) {
        if (audio_index < audioArrayList.size - 1) {
            audio_index++
            playAudio(audio_index)
            Log.d(TAG, "play audio index - $audio_index")
        } else {
            audio_index = 0
            playAudio(audio_index)
        }
    }

    //pause audio
    private fun setPause(audioArrayList: List<RoomAudioModel>) {
        Log.d(TAG,"setPause1 - " + musicService!!.mediaPlayer)
        Log.d(TAG,"setPause2 - " + musicService!!.mediaPlayer?.isPlaying)
        if (musicService!!.isPlaying()) {
            musicService!!.pause()
            binding.playPause.setImageResource(R.drawable.ic_play_button_arrowhead)
            CreateNotification().createNotification(this, audioArrayList[audio_index], R.drawable.ic_play_button_arrowhead)
        } else {
            musicService!!.start()
            binding.playPause.setImageResource(R.drawable.ic_pause)
            CreateNotification().createNotification(this, audioArrayList[audio_index], R.drawable.ic_pause)
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

}