package com.example.newdesignmusicplayer.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface RoomDao {

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertMusic(roomAudioModel: RoomAudioModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusics(musics: List<RoomAudioModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(roomFolderModel: RoomFolderModel)

    @Query("UPDATE RoomFolderModel SET folderName = :newFolderName WHERE folderName = :oldFolderName")
    suspend fun setNewFolderName(newFolderName: String, oldFolderName: String)

    @Query("UPDATE ROOMAUDIOMODEL SET isFavorite= :intState WHERE id = :position")
    suspend fun setFavorite(intState:Int,position: Int)

    @Query("SELECT EXISTS ( SELECT * FROM roomfoldermodel WHERE folderName = :newFolderName)")
    suspend fun checkForExists(newFolderName: String):Boolean

    @Query("select * from roomaudiomodel")
    fun getMusics():LiveData<List<RoomAudioModel>>

    @Query("select * from roomfoldermodel")
    fun getFolders():LiveData<List<RoomFolderModel>>

    @Query("SELECT COUNT(*) FROM roomfoldermodel")
    suspend fun getFoldersCount():Int

    @Query("SELECT * FROM roomfoldermodel WHERE folderName = :name ")
    suspend fun getFolder(name: String):RoomFolderModel

    @Query("SELECT * FROM roomaudiomodel WHERE id = :position")
    fun getMusic(position:Int):RoomAudioModel

    @Delete()
    suspend fun deleteFolder(roomFolderModel: RoomFolderModel)

    @Update
    suspend fun updateFolder(folder:RoomFolderModel )

}