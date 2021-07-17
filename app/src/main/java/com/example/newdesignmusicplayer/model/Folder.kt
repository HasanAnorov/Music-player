package com.example.newdesignmusicplayer.model

import android.view.Display
import com.example.newdesignmusicplayer.room.RoomAudioModel
import java.io.Serializable

class Folder(
    var folderName :String? = null,
    var musicList:List<RoomAudioModel>
    ):Serializable