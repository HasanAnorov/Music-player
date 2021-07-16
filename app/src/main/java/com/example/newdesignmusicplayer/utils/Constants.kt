package com.example.newdesignmusicplayer.utils

import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.model.ModelAudio

object Constants {

    const val STORAGE_PERMISSION_CODE = 1
    private val allFolders :ArrayList<Folder> = arrayListOf()

    fun setFolders(folder:Folder){
        allFolders.add(folder)
    }
    fun getFolders():ArrayList<Folder>{
        return allFolders
    }

    fun setFavorite(item:ModelAudio){

    }

    fun getFavorites():ArrayList<ModelAudio>{
        return arrayListOf()
    }

    fun checkForFavorite(item:ModelAudio):Boolean{
        return true
    }

}