package com.example.newdesignmusicplayer.room

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.*

@Entity(tableName = "folder")
data class RoomFolderModel(
        @PrimaryKey(autoGenerate = true)
        var id:Int? = null,
        @ColumnInfo(name = "folderName")
        var folderName :String? = null,
//        @ColumnInfo(name = "musicList")
//        var musicList:List<RoomAudioModel>
):Serializable

