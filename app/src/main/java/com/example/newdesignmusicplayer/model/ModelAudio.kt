package com.example.newdesignmusicplayer.model

import java.io.Serializable

//class ModelAudio(
//    var audioTitle: String? = null,
//    var audioDuration: String? = null,
//    var audioArtist: String? = null,
//    var audioUri: String? = null
//
//):Serializable

class ModelAudio():Serializable{

    var audioTitle: String? = null
    var audioDuration: String? = null
    var audioArtist: String? = null
    var audioUri: String? = null
    var audioPhoto :String? = null


    fun getaudioTitle(): String? {
        return audioTitle
    }

    fun setaudioTitle(audioTitle: String?) {
        this.audioTitle = audioTitle
    }

    fun getaudioDuration(): String? {
        return audioDuration
    }

    fun setPhoto(audioPhoto:String?){
        this.audioPhoto = audioPhoto
    }

    fun setaudioDuration(audioDuration: String?) {
        this.audioDuration = audioDuration
    }

    fun getaudioArtist(): String? {
        return audioArtist
    }

    fun setaudioArtist(audioArtist: String?) {
        this.audioArtist = audioArtist
    }

    fun getaudioUri(): String? {
        return audioUri
    }

    fun setaudioUri(audioUri: String) {
        this.audioUri = audioUri
    }
}