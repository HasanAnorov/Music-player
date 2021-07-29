package com.example.newdesignmusicplayer.interfaces

import com.example.newdesignmusicplayer.room.RoomFolderModel
import java.text.FieldPosition

interface OnFolderForSelection {
    fun onFolderForSelectionClick(model:RoomFolderModel,position: Int)
}