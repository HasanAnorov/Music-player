package com.example.newdesignmusicplayer.room

import androidx.room.*

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusic(roomAudioModel : RoomAudioModel)

    @Query("select * from roomaudiomodel")
    fun getMusics():List<RoomAudioModel>

    @Delete()
    fun delete(roomAudioModel: RoomAudioModel)
}