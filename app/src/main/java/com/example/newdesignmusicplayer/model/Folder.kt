package com.example.newdesignmusicplayer.model

import android.view.Display
import java.io.Serializable

class Folder(
    var folderName :String? = null,
    var musicList:ArrayList<ModelAudio>
    ):Serializable