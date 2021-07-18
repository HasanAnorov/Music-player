package com.example.newdesignmusicplayer.room

import androidx.room.*

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusic(roomAudioModel : RoomAudioModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFolder(roomFolderModel: RoomFolderModel)

    @Query("select * from roomaudiomodel")
    fun getMusics():List<RoomAudioModel>

    @Query("select * from folder")
    fun getFolders():List<RoomFolderModel>?

    @Query("SELECT * FROM folder WHERE folderName LIKE :name ")
    fun getFolder(name :String):RoomFolderModel

    @Delete()
    fun deleteFolder(roomFolderModel: RoomFolderModel)

    @Delete()
    fun deleteMusic(roomAudioModel: RoomAudioModel)
}