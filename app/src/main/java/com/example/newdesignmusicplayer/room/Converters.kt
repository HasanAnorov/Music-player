package com.example.newdesignmusicplayer.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromList(list:List<RoomAudioModel>) : String{
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun toList(string:String):List<RoomAudioModel>{
        val gson = Gson()
        val type = object : TypeToken<List<RoomAudioModel>>() {}.type
        return gson.fromJson(string,type)
    }

}