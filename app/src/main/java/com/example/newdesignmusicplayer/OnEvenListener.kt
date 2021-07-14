package com.example.newdesignmusicplayer

import android.view.View
import com.example.newdesignmusicplayer.model.ModelAudio
import java.text.FieldPosition

interface OnEvenListener {
    fun onMenuItemClick(model:ModelAudio,position: Int,view:View)
}