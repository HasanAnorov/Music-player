package com.example.newdesignmusicplayer.Services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class OnClearFromRecentService :Service(){

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }

}
