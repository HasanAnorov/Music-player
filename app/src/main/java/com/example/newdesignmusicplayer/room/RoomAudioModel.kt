package com.example.newdesignmusicplayer.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomAudioModel (
    var audioTitle: String? = null,
    var audioDuration: String? = null,
    var audioArtist: String? = null,
    var audioUri: String? = null,
    var isFavorite:Boolean = false
){
    @PrimaryKey(autoGenerate = true)
    var id :Int = 0
}