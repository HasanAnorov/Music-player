package com.example.newdesignmusicplayer

import android.view.View
import com.example.newdesignmusicplayer.room.RoomFolderModel
import java.io.Serializable

interface OnFolderListener:Serializable {
    fun onFolderItemClick(view:View,folder:RoomFolderModel,position:Int)
    fun onFolderClick(folder:RoomFolderModel)
}