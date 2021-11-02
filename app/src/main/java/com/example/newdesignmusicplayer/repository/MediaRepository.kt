package com.example.newdesignmusicplayer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newdesignmusicplayer.room.RoomAudioModel
import com.example.newdesignmusicplayer.room.RoomDao
import com.example.newdesignmusicplayer.room.RoomFolderModel

class MediaRepository (private val mediaDao: RoomDao){

    //val musics : LiveData<List<RoomAudioModel>> = mediaDao.getMusics()
    //val folders: List<RoomFolderModel> = mediaDao.getFolders()
    //    suspend fun insertMusic(roomAudioModel: RoomAudioModel){
    //        mediaDao.insertMusic(roomAudioModel)
    //    }

    fun getFolders():LiveData<List<RoomFolderModel>> = mediaDao.getFolders()

    fun getMusics():LiveData<List<RoomAudioModel>> = mediaDao.getMusics()

    suspend fun getFoldersCount():Int = mediaDao.getFoldersCount()

    suspend fun checkForExist(newFolderName: String):Boolean = mediaDao.checkForExists(newFolderName)

    fun getFolder(name: String):RoomFolderModel = mediaDao.getFolder(name)

    fun getMusic(position:Int) = mediaDao.getMusic(position)

    suspend fun setFavorite(intState:Int, musicTitle: String){
        mediaDao.setFavorite(intState,musicTitle)
    }

    suspend fun insertMusics(musics:List<RoomAudioModel>){
        mediaDao.insertMusics(musics)
    }

    suspend fun updateFolder(folder:RoomFolderModel ){
        mediaDao.updateFolder(folder)
    }

    suspend fun setNewFolderName(newFolderName: String, oldFolderName: String){
        mediaDao.setNewFolderName(newFolderName,oldFolderName)
    }

    suspend fun deleteFolder(roomFolderModel: RoomFolderModel){
        mediaDao.deleteFolder(roomFolderModel)
    }

    suspend fun insertFolder(roomFolderModel: RoomFolderModel){
        mediaDao.insertFolder(roomFolderModel)
    }


}