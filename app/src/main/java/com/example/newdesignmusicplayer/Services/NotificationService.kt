package com.example.newdesignmusicplayer.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationService:BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sendBroadcast(Intent("TRACKS_TRACKS").putExtra("actionName",intent?.action))
    }

}