package com.example.newdesignmusicplayer.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.newdesignmusicplayer.R
import com.example.newdesignmusicplayer.adapter.FolderAdapter
import com.example.newdesignmusicplayer.databinding.ActivityMainBinding
import com.example.newdesignmusicplayer.interfaces.OnFolderListener
import com.example.newdesignmusicplayer.room.RoomAudioModel
import com.example.newdesignmusicplayer.room.RoomDbHelper
import com.example.newdesignmusicplayer.room.RoomFolderModel
import com.example.newdesignmusicplayer.viewmodel.MediaViewModel
import com.github.zawadz88.materialpopupmenu.popupMenu
import java.io.Serializable
import java.util.*

class MainActivity : AppCompatActivity(), OnFolderListener,Serializable {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FolderAdapter
    val STORAGE_PERMISSION_CODE = 1
    private lateinit var dbHelper: RoomDbHelper
    private lateinit var viewModel:MediaViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        viewModel = ViewModelProvider(this).get(MediaViewModel::class.java)

        dbHelper = RoomDbHelper.DatabaseBuilder.getInstance(this)
        adapter = FolderAdapter(this)
        dbHelper.roomDao().getFolders()?.let { setAdapter(it) }
        binding.cardMenu.elevation = 0F

        // status bar text color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or  View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        }

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = getColor(R.color.main_light)
            window.navigationBarColor = getColor(R.color.white)
        }

        //checking permission
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }

        binding.cardMenu.setOnClickListener {
            val dialog = AlertDialog.Builder(this).create()
            val dialogView = layoutInflater.inflate(R.layout.adding_folder_dialog_new, binding.root, false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setView(dialogView)

            val ok = dialogView.findViewById<CardView>(R.id.yes)
            ok.elevation = 0F
            val no = dialogView.findViewById<CardView>(R.id.no)
            no.elevation = 0F
            val et = dialogView.findViewById<EditText>(R.id.textInputEditText)
            et.requestFocus()
            et.isFocusableInTouchMode = true
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.show()

            no.setOnClickListener {
                dialog.dismiss()
            }
            ok.setOnClickListener {
                val folderName = et.text.toString()
                if (et.text.isNullOrEmpty()){
                    et.error = "Fill field"
                }else{
                    //do mentioned
                    val newRoomFolder = RoomFolderModel(folderName = folderName,audioList = listOf<RoomAudioModel>())
                    dbHelper.roomDao().insertFolder(newRoomFolder)
                    adapter.notifyItemInserted(dbHelper.roomDao().getFoldersCount()-1)
                    //adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
        }
    }

    private  fun setAdapter(folders:List<RoomFolderModel>){
        adapter.differ.submitList(folders)
        binding.recyclerView.adapter=adapter
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val contentResolver = this.contentResolver
                    val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)

                    //looping through all rows and adding to list
                    when{
                        cursor == null -> {
                            // query failed, handle error.
                            Toast.makeText(this, "Cannot read music !", Toast.LENGTH_SHORT).show()

                        }
                        !cursor.moveToFirst() -> {
                            // no media on the device
                            Toast.makeText(this, "No music found on this phone", Toast.LENGTH_SHORT).show()

                        }
                        else ->{
                            do {
                                val title: String = cursor.getString(cursor.getColumnIndex(
                                    MediaStore.Audio.Media.TITLE))
                                val artist: String = cursor.getString(cursor.getColumnIndex(
                                    MediaStore.Audio.Media.ARTIST))
                                val url: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                                val roomAudio= RoomAudioModel(audioTitle = title,audioDuration = "null",audioArtist = artist,audioUri = url,isFavorite = 0,isSelected = false)
                                dbHelper.roomDao().insertMusic(roomAudio)

                            } while (cursor.moveToNext())
                            cursor.close()
                        }
                    }

                dbHelper.roomDao().insertFolder(RoomFolderModel(folderName = "Your musics",audioList = dbHelper.roomDao().getMusics()))
                dbHelper.roomDao().insertFolder(RoomFolderModel(folderName = "Favorites",audioList = listOf<RoomAudioModel>()))

                val folders = dbHelper.roomDao().getFolders()
                if (folders != null) {
                    setAdapter(folders)
                }
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFolderItemClick(view:View,folder:RoomFolderModel,position: Int) {
        val popupMenu = popupMenu {
            style = R.style.Widget_MPM_Menu_Dark_CustomBackground
            section {
                item {
                    label = "Rename"
                    labelColor = ContextCompat.getColor(this@MainActivity, R.color.folderActivity)
                    icon = R.drawable.ic_edit__2_ //optional
                    iconColor = ContextCompat.getColor(this@MainActivity, R.color.folderActivity)
                    callback = { //optional
                        val dialog = AlertDialog.Builder(this@MainActivity).create()
                        val dialogView = layoutInflater.inflate(R.layout.change_folder_dialog, binding.root, false)
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setView(dialogView)

                        val ok = dialogView.findViewById<CardView>(R.id.yes)
                        ok.elevation = 0F
                        val no = dialogView.findViewById<CardView>(R.id.no)
                        no.elevation = 0F
                        val et = dialogView.findViewById<EditText>(R.id.textInputEditText)
                        et.requestFocus()
                        et.isFocusableInTouchMode = true
                        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                        dialog.show()

                        no.setOnClickListener {
                            dialog.dismiss()
                        }
                        ok.setOnClickListener {
                            val folderName = et.text.toString()
                            if (et.text.isNullOrEmpty()){
                                et.error = "Fill field"
                            }
                            if (dbHelper.roomDao().checkForExists(folderName)){
                                et.error = "Folder name exists !"
                            }
                            else{
                                dbHelper.roomDao().setNewFolderName(folderName,folder.folderName)
                                folder.folderName = folderName
                                adapter.notifyDataSetChanged()
                                dialog.dismiss()
                            }
                        }
                    }
                }
                item {
                    labelRes = R.string.remove
                    labelColor = ContextCompat.getColor(this@MainActivity, R.color.folderActivity)
                    iconDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_trash) //optional
                    iconColor =ContextCompat.getColor(this@MainActivity, R.color.folderActivity)
                    callback = {

                        dbHelper.roomDao().deleteFolder(folder)
                        adapter.notifyDataSetChanged()
                        //adapter.notifyItemRemoved(position)
                        //Toast.makeText(this@MainActivity, position, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        popupMenu.show(this, view)
    }

    override fun onFolderClick(folder: RoomFolderModel) {
            val intent = Intent(this, FolderActivity::class.java)
            intent.putExtra("folderName",folder.folderName)
            startActivity(intent)
    }
}