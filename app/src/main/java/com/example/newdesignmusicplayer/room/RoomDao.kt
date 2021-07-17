package com.example.newdesignmusicplayer.room

import androidx.room.*

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFolder(roomFolderModel: RoomFolderModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusic(roomAudioModel : RoomAudioModel)

    @Query("select * from roomaudiomodel")
    fun getMusics():List<RoomAudioModel>

    @Query("select * from folder")
    fun getFolders():List<RoomFolderModel>

    @Delete()
    fun delete(roomAudioModel: RoomAudioModel)
}