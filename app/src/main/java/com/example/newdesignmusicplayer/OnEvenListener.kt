package com.example.newdesignmusicplayer

import android.view.View
import com.example.newdesignmusicplayer.room.RoomAudioModel
import java.io.Serializable

interface OnEvenListener : Serializable {
    fun onMenuItemClick(model:RoomAudioModel,position: Int,view:View)
}