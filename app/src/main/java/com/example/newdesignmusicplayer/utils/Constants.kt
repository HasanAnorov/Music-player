package com.example.newdesignmusicplayer.utils

import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.room.RoomAudioModel

object Constants {

    const val STORAGE_PERMISSION_CODE = 1
    private val allFolders :ArrayList<Folder> = arrayListOf()

    fun setFolders(folder:Folder){
        allFolders.add(folder)
    }
    fun getFolders():ArrayList<Folder>{
        return allFolders
    }

    fun setFavorite(item: RoomAudioModel){

    }

    fun getFavorites():ArrayList<RoomAudioModel>{
        return arrayListOf()
    }

    fun checkForFavorite(item:RoomAudioModel):Boolean{
        return true
    }

}