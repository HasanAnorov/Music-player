package com.example.newdesignmusicplayer.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class RoomAudioModel (
    var audioTitle: String,
    var audioDuration: String,
    var audioArtist: String,
    var audioUri: String,
    var isFavorite:Int,
    var isSelected:Boolean
):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id :Int = 0
}