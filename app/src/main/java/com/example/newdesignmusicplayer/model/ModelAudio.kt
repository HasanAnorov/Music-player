package com.example.newdesignmusicplayer.model

import java.io.Serializable

class ModelAudio():Serializable{

    //var id :String? = null
    var audioTitle: String? = null
    var audioDuration: String? = null
    var audioArtist: String? = null
    var audioUri: String? = null
    var isPlaying:Boolean = false

//    fun getAudioId(): String? {
//        return id
//    }
//
//    fun setAudioId(id: String?) {
//        this.id = id
//    }

    fun getIsPlaying(): Boolean {
        return isPlaying
    }

    fun setIsPlaying(isPlaying: Boolean) {
        this.isPlaying = isPlaying
    }

    fun getaudioTitle(): String? {
        return audioTitle
    }

    fun setaudioTitle(audioTitle: String?) {
        this.audioTitle = audioTitle
    }

    fun getaudioDuration(): String? {
        return audioDuration
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