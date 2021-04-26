package com.example.newdesignmusicplayer

import android.Manifest
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newdesignmusicplayer.databinding.ActivityMusicBinding
import com.example.newdesignmusicplayer.model.ModelAudio
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class MusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var audioArrayList: ArrayList<ModelAudio>
    var current_pos = 0.0
    private var total_duration: Double = 0.0
    private var audio_index = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val position = intent.getIntExtra("pos", 0)

        checkPermissions()
        setAudio(position)


        binding.btnArrow.setOnClickListener {
           onBackPressed()
        }
    }

    //setting audio files
    private fun setAudio(pos: Int) {

    var isRepeatActivated = false
    var isRandomPlayingActivated = false

        audioArrayList = intent.getSerializableExtra("musics") as ArrayList<ModelAudio>
        mediaPlayer = MediaPlayer()
        audio_index = pos

    binding.shuffle.setOnClickListener {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
        isRandomPlayingActivated =!isRandomPlayingActivated
        if (isRepeatActivated){
            isRepeatActivated = false
        }
    }

    binding.repeat.setOnClickListener{
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()

        isRepeatActivated = !isRepeatActivated
        if (isRandomPlayingActivated){
            isRandomPlayingActivated = false
        }
    }

        //seekbar change listner
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
            playAudio(audio_index)
        } else {
            audio_index = 0
            playAudio(audio_index)
        }
    }

    if (audioArrayList.isNotEmpty()) {
            playAudio(audio_index)
            prevAudio()
            nextAudio()
            setPause()
        }

}

    //play audio file
    private fun playAudio(pos: Int) {
        try {
            //mediaPlayer = MediaPlayer()
            mediaPlayer.reset()
            //set file path
            mediaPlayer.setDataSource(this, Uri.parse(audioArrayList[pos].getaudioUri()!!))
            mediaPlayer.prepare()
            mediaPlayer.start()
            binding.playPause.setImageResource(R.drawable.ic_pause)
            //binding.playPause.setBackgroundColor(R.id.white)
            binding.musicName.text = audioArrayList[pos].getaudioTitle()
            binding.musicAuthor.text = audioArrayList[pos].getaudioArtist()
            binding.musicDuration.text = audioArrayList[pos].getaudioDuration()
            audio_index = pos

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
    private fun prevAudio() {
        binding.playPrevious.setOnClickListener {
            if (audio_index > 0) {
                audio_index--
                playAudio(audio_index)
            } else {
                audio_index = audioArrayList.size - 1
                playAudio(audio_index)
            }
        }
    }

    //play next audio
    private fun nextAudio() {
        binding.playNext.setOnClickListener {
            if (audio_index < audioArrayList.size - 1) {
                audio_index++
                playAudio(audio_index)
            } else {
                audio_index = 0
                playAudio(audio_index)
            }
        }
    }

    //pause audio
    private fun setPause() {
        binding.playPause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.playPause.setImageResource(R.drawable.ic_play_button_arrowhead)
            } else {
                mediaPlayer.start()
                binding.playPause.setImageResource(R.drawable.ic_pause)
            }
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

    //release mediaplayer
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer = MediaPlayer()
        mediaPlayer.reset()
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