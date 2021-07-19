package com.example.newdesignmusicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.newdesignmusicplayer.adapter.MusicListAdapter
import com.example.newdesignmusicplayer.databinding.ActivityFolderBinding
import com.example.newdesignmusicplayer.room.RoomAudioModel
import com.example.newdesignmusicplayer.room.RoomDbHelper
import com.github.zawadz88.materialpopupmenu.popupMenu
import java.io.Serializable
import java.util.*

class FolderActivity : AppCompatActivity(),Serializable,OnEvenListener {

    private lateinit var binding: ActivityFolderBinding
    private lateinit var adapter: MusicListAdapter
    private lateinit var musicList :List<RoomAudioModel>
    private lateinit var dbHelper: RoomDbHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        dbHelper = RoomDbHelper.DatabaseBuilder.getInstance(this)

        // status bar text color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        }

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = getColor(R.color.folderActivity)
            window.navigationBarColor = getColor(R.color.white)

        }

        val folderName = intent.getStringExtra("folderName") as String
        val folder = dbHelper.roomDao().getFolder(folderName)
        musicList = folder.audioList


        binding.textView.text = "${musicList.size} tracks"
        binding.tvFolderName.text = folderName
        binding.btnArrow.elevation = 0F

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onQueryTextChange(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

           setAdapter(musicList,folderName)

        binding.btnArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setAdapter(musicList:List<RoomAudioModel>,folderNameMusic:String){
        adapter =MusicListAdapter(this,this){  position: Int ->

            val intent = Intent(this, MusicActivity::class.java)
            intent.putExtra("folderName",folderNameMusic)
            intent.putExtra("position", position)
            startActivity(intent)
        }

        adapter.differ.submitList(musicList)
        binding.recyclerView.adapter = adapter
    }

    fun onQueryTextChange(newText: String){
        val userInput = newText.toLowerCase(Locale.ROOT)
        val myFiles = arrayListOf<RoomAudioModel>()
        for (song in musicList) {
            if (song.audioTitle.toLowerCase(Locale.ROOT).contains(userInput)){
                myFiles.add(song)
            }
        }
         //musicList = myFiles
         adapter.differ.submitList(myFiles)
    }

    override fun onMenuItemClick(model: RoomAudioModel, position: Int,view:View) {
        val popupMenu = popupMenu {
            style = R.style.Widget_MPM_Menu_Dark_CustomBackground
            section {
                item {
                    label = "Add to"
                    labelColor = ContextCompat.getColor(this@FolderActivity,R.color.folderActivity)
                    icon = R.drawable.ic_add__4_ //optional
                    iconColor = ContextCompat.getColor(this@FolderActivity,R.color.folderActivity)
                    callback = {
                        //Do something to move!
                        Toast.makeText(this@FolderActivity, "Do something to move!", Toast.LENGTH_SHORT).show()
                    }
                }
                item {
                    labelRes = R.string.remove
                    labelColor = ContextCompat.getColor(this@FolderActivity,R.color.folderActivity)
                    iconDrawable = ContextCompat.getDrawable(this@FolderActivity, R.drawable.ic_trash) //optional
                    iconColor =ContextCompat.getColor(this@FolderActivity,R.color.folderActivity)
                    callback = {
                        //Do something to remove folder!
                        Toast.makeText(this@FolderActivity, "Do something to remove folder!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        popupMenu.show(this, view)
    }
}