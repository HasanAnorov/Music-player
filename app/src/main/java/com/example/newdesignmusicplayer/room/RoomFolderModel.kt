package com.example.newdesignmusicplayer.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "folder")
data class RoomFolderModel (
        @ColumnInfo(name = "folderName")
        var folderName :String ,
        @ColumnInfo(name = "audioList")
        var audioList:List<RoomAudioModel>
):Serializable{
        @PrimaryKey(autoGenerate = true)
        var id :Int =0
}