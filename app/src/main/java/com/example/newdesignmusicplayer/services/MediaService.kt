package com.example.newdesignmusicplayer.services

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.nfc.Tag
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.newdesignmusicplayer.repository.MediaRepository
import com.example.newdesignmusicplayer.room.RoomAudioModel
import com.example.newdesignmusicplayer.room.RoomDbHelper
import com.example.newdesignmusicplayer.ui.MusicActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.function.LongFunction
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import android.os.Bundle
import android.provider.MediaStore
import com.example.newdesignmusicplayer.services.MediaService.LocalBinder

class MediaService:Service(),OnCompletionListener {

    var TAG = "MEDIA_SERVICE"
    var mediaPlayer: MediaPlayer? = null
    var musicFiles: List<RoomAudioModel>? = null
    // Binder given to clients
    private val binder = LocalBinder()
    var mediaIndex = 0



    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate")
        musicFiles = listOf()
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG,"onBind")
        return binder
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): MediaService = this@MediaService
    }

    /** By this code you can send data to activity by broadcast manager */
    private fun sendMessageToActivity( msg: String) {
        val intent = Intent("MediaPlayerDurationUpdates")
        intent.putExtra("mediaDuration", msg)
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val folderName = intent?.getStringExtra("folder_name")
        val position = intent?.getIntExtra("service_position",0)

        musicFiles = getMusicsFromDB(folderName)
        mediaIndex = position!!.toInt()

        //playMedia(position)

        /**this relates to 53 line code (broadcast manager) */
//        Log.d(TAG,"checking broadcast - " + mediaPlayer?.duration )
//        sendMessageToActivity(mediaPlayer?.duration.toString())
        return  START_STICKY
    }

    private fun getMusicsFromDB(folder_name: String?): List<RoomAudioModel> {
        val mediaDao = RoomDbHelper.DatabaseBuilder.getInstance(application).roomDao()
        val repository = MediaRepository(mediaDao)
        return repository.getFolder(folder_name ?: "Your musics").audioList
    }

    fun pause(){
        mediaPlayer?.pause()
    }

    fun start(){
        mediaPlayer?.start()
    }

    fun duration(): Double? {
        return mediaPlayer?.duration?.toDouble()
    }

    fun currentPosition(): Double? {
        Log.d(TAG,"mediaPlayer - " + mediaPlayer + " - return - " + mediaPlayer!!.currentPosition)
        return mediaPlayer?.currentPosition?.toDouble()
    }

    fun seekTo(position:Int){
        mediaPlayer?.seekTo(position)
    }

    fun isPlaying(): Boolean {
        Log.d(TAG,"isPlaying - " + mediaPlayer?.isPlaying + "   -   " + mediaPlayer)
        return mediaPlayer!!.isPlaying
    }

    fun onCompleted(){
        mediaPlayer?.setOnCompletionListener(this)
    }

     fun playMedia(startPosition:Int){
         Log.d(TAG, "playMedia - $startPosition")
        if (mediaPlayer!=null){
            Log.d(TAG,"playMedia_0 ")
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            createMediaPlayer(startPosition,musicFiles!!)
            sendMessageToActivity(mediaPlayer?.duration.toString())
        }else{
            Log.d(TAG,"playMedia_1 ")
            mediaPlayer = MediaPlayer()
            Log.d(TAG, "given position - $startPosition")
            createMediaPlayer(startPosition,musicFiles!!)
            sendMessageToActivity(mediaPlayer?.duration.toString())
        }
    }

    private fun createMediaPlayer(position: Int, musicFiles: List<RoomAudioModel>) {
        mediaPlayer?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(applicationContext, Uri.parse(musicFiles[position].audioUri))
                prepare()
                start()
            }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG,"onCompletion")
    }

//    //play previous audio
//    private fun prevAudio(audioArrayList: List<RoomAudioModel>) {
//        if (audio_index > 0) {
//            audio_index--
//            playAudio(audio_index)
//        } else {
//            audio_index = audioArrayList.size - 1
//            playAudio(audio_index)
//        }
//    }
//
//    //play next audio
//    private fun nextAudio(audioArrayList: List<RoomAudioModel>) {
//        if (audio_index < audioArrayList.size - 1) {
//            audio_index++
//            playAudio(audio_index)
//            Log.d(TAG, "play audio index - $audio_index")
//        } else {
//            audio_index = 0
//            playAudio(audio_index)
//        }
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d(TAG,"before onDestroy - $mediaPlayer")
//        mediaPlayer?.stop()
//        mediaPlayer?.release()
//        mediaPlayer = null
//        Log.d(TAG,"after onDestroy - $mediaPlayer")
//    }

}