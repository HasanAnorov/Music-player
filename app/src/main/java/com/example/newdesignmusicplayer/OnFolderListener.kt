package com.example.newdesignmusicplayer

import android.view.View
import com.example.newdesignmusicplayer.model.Folder

interface OnFolderListener {
    fun onFolderItemClick(view:View,folder:Folder,position:Int)
}