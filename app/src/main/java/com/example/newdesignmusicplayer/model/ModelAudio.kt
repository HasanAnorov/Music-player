package com.example.newdesignmusicplayer.model

import android.net.Uri
import android.os.Parcelable
import java.io.Serializable

class ModelAudio(
    var audioTitle: String? = null,
    var audioDuration: String? = null,
    var audioArtist: String? = null,
    var audioUri: Uri? = null

):Serializable

//class ModelAudio(){
//
//    var audioTitle: String? = null
//    var audioDuration: String? = null
//    var audioArtist: String? = null
//    var audioUri: Uri? = null
//
//
//
//    fun getaudioTitle(): String? {
//        return audioTitle
//    }
//
//    fun setaudioTitle(audioTitle: String?) {
//        this.audioTitle = audioTitle
//    }
//
//    fun getaudioDuration(): String? {
//        return audioDuration
//    }
//
//    fun setaudioDuration(audioDuration: String?) {
//        this.audioDuration = audioDuration
//    }
//
//    fun getaudioArtist(): String? {
//        return audioArtist
//    }
//
//    fun setaudioArtist(audioArtist: String?) {
//        this.audioArtist = audioArtist
//    }
//
//    fun getaudioUri(): Uri? {
//        return audioUri
//    }
//
//    fun setaudioUri(audioUri: Uri?) {
//        this.audioUri = audioUri
//    }
//}