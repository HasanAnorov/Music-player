package com.example.newdesignmusicplayer.room

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusic(roomAudioModel: RoomAudioModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFolder(roomFolderModel: RoomFolderModel)

    @Query("UPDATE RoomFolderModel SET folderName = :newFolderName WHERE folderName = :oldFolderName")
    fun setNewFolderName(newFolderName: String, oldFolderName: String)

    @Query("SELECT EXISTS ( SELECT * FROM roomfoldermodel WHERE folderName = :newFolderName)")
    fun checkForExists(newFolderName: String):Boolean

    @Query("select * from roomaudiomodel")
    fun getMusics():List<RoomAudioModel>

    @Query("select * from roomfoldermodel")
    fun getFolders():List<RoomFolderModel>?

    @Query("SELECT COUNT(*) FROM roomfoldermodel")
    fun getFoldersCount():Int

    @Query("SELECT * FROM roomfoldermodel WHERE folderName = :name ")
    fun getFolder(name: String):RoomFolderModel

    @Query("SELECT * FROM roomaudiomodel WHERE id = :position")
    fun getMusic(position:Int):RoomAudioModel

    @Delete()
    fun deleteFolder(roomFolderModel: RoomFolderModel)

}