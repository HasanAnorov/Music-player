package com.example.newdesignmusicplayer.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.model.ModelAudio

object Constants {

    const val STORAGE_PERMISSION_CODE = 1
    private val allFolders :ArrayList<Folder> = arrayListOf()

    //fetch the audio files from storage
    fun fetchAudioFiles(context:Context):ArrayList<ModelAudio>{
        val contentResolver = context.contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = contentResolver?.query(uri, null, null, null, null)
        val returningFiles = ArrayList<ModelAudio>()

        //looping through all rows and adding to list
        when{
            cursor == null -> {
                // query failed, handle error.
                Toast.makeText(context, "Cannot read music", Toast.LENGTH_SHORT).show()
                return returningFiles
            }
            !cursor.moveToFirst() -> {
                // no media on the device
                Toast.makeText(context, "No music found on this phone", Toast.LENGTH_SHORT).show()
                return returningFiles
            }
            else ->{
                do {
                    val id: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val title: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val artist: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val url: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    returningFiles.add(ModelAudio(id, title, "null", artist, url, false, false))
                } while (cursor.moveToNext())
                cursor.close()
                return  returningFiles
            }
        }
    }
    
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