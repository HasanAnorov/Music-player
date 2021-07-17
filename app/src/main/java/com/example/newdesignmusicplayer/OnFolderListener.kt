package com.example.newdesignmusicplayer

import android.view.View
import com.example.newdesignmusicplayer.room.RoomFolderModel

interface OnFolderListener {
    fun onFolderItemClick(view:View,folder:RoomFolderModel,position:Int)
}