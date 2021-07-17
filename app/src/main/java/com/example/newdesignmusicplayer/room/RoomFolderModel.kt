package com.example.newdesignmusicplayer.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "folder")
data class RoomFolderModel (
        @ColumnInfo(name = "folderName")
        var folderName :String? = null,
        @ColumnInfo(name = "audioList")
        var audioList:List<RoomAudioModel>? = null
):Serializable{
        @PrimaryKey(autoGenerate = true)
        var id :Int = 0
}