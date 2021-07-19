package com.example.newdesignmusicplayer.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomAudioModel (
    var audioTitle: String,
    var audioDuration: String,
    var audioArtist: String,
    var audioUri: String,
    var isFavorite:Int
){
    @PrimaryKey(autoGenerate = true)
    var id :Int = 0
}