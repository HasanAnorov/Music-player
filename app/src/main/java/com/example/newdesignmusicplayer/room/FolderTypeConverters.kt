package com.example.newdesignmusicplayer.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


//class FolderTypeConverters {
//
//    var gson = Gson()
//
//    @TypeConverter
//    fun stringToFolderList(data: String?): List<RoomFolderModel?>? {
//        if (data == null) {
//            return Collections.emptyList()
//        }
//        val listType = object : TypeToken<List<RoomFolderModel?>?>() {}.type
//        return gson.fromJson<List<RoomFolderModel?>>(data, listType)
//    }
//
//    @TypeConverter
//    fun folderListToString(someObjects: List<RoomFolderModel?>?): String? {
//        return gson.toJson(someObjects)
//    }
//
//}