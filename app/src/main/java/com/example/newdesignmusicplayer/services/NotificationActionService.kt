package com.example.newdesignmusicplayer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationActionService: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sendBroadcast(Intent("TRACKS_TRACKS")
            .putExtra("actionname",intent?.action))
    }

}