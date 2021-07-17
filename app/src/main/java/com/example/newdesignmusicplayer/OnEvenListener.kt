package com.example.newdesignmusicplayer

import android.view.View
import com.example.newdesignmusicplayer.room.RoomAudioModel

interface OnEvenListener {
    fun onMenuItemClick(roomModel: RoomAudioModel, position: Int, view:View)
}