package com.example.newdesignmusicplayer.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RoomAudioModel::class,RoomFolderModel::class], version = 3,exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoomDbHelper : RoomDatabase() {

    abstract fun roomDao():RoomDao

    object DatabaseBuilder {
        private var instance :RoomDbHelper? = null

        fun getInstance(context: Context):RoomDbHelper{
            if (instance == null){
                synchronized(RoomDbHelper::class.java){
                    instance = buildRoomDb(context)
                }
            }
            return instance!!
        }

        private fun buildRoomDb(context: Context):RoomDbHelper = Room.databaseBuilder(
            context.applicationContext,
            RoomDbHelper::class.java,
            "media_player.db")
            .allowMainThreadQueries()
            .build()
    }
}