package com.example.newdesignmusicplayer

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.newdesignmusicplayer.Services.NotificationActionService
import com.example.newdesignmusicplayer.model.ModelAudio


class CreateNotification {
    var CHANNEL_ID = "channel1"

    val ACTION_PREVIOUS = "action_previous"
    val ACTION_PLAY = "action_play"
    val ACTION_NEXT = "action_next"

    lateinit var notification: Notification

    fun createNotification(context: Context,track:ModelAudio,playButton:Int){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            val mediaSessionCompat = MediaSessionCompat(context, "tag")

            val icon = BitmapFactory.decodeResource(context.resources,R.drawable.music_photo)

            val pendingIntentPrevious: PendingIntent?
            val drw_previous: Int?
//            if (position==0){
//                pendingIntentPrevious = null
//                drw_previous = 0
//            }else{
                val intentPrevious = Intent(context,NotificationActionService::class.java)
                        .setAction(ACTION_PREVIOUS)
                pendingIntentPrevious = PendingIntent.getBroadcast(context,
                    0,
                    intentPrevious,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                drw_previous = R.drawable.ic_baseline_skip_previous_24
            //}

            val intentPLay = Intent(context,NotificationActionService::class.java)
                    .setAction(ACTION_PLAY)
            val pendingIntentPlay = PendingIntent.getBroadcast(context,
                0,
                intentPLay,
                PendingIntent.FLAG_UPDATE_CURRENT)


            val pendingIntentNext: PendingIntent?
            val drw_next: Int?

//            if (position==size){
//                pendingIntentNext = null
//                drw_next = 0
//            }else{
                val intentNext = Intent(context,NotificationActionService::class.java)
                        .setAction(ACTION_NEXT)
                pendingIntentNext = PendingIntent.getBroadcast(context,
                    0,
                    intentNext,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                drw_next = R.drawable.ic_baseline_skip_next_24
            //}

            //create notification
            notification = NotificationCompat.Builder(context,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_musical_note)
                    .setContentTitle(track.audioTitle)
                    .setContentText(track.audioArtist)
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)//show only first time
                    .setShowWhen(false)
                    .addAction(drw_previous,"Previous",pendingIntentPrevious)
                    .addAction(playButton,"Play",pendingIntentPlay)
                    .addAction(drw_next,"Next",pendingIntentNext)
                    .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2)
                            .setMediaSession(mediaSessionCompat.sessionToken))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build()

            notificationManagerCompat.notify(1,notification)
        }
    }
}