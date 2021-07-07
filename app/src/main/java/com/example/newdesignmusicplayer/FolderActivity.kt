package com.example.newdesignmusicplayer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.newdesignmusicplayer.adapter.MusicListAdapter
import com.example.newdesignmusicplayer.databinding.ActivityFolderBinding
import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.model.ModelAudio
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class FolderActivity : AppCompatActivity(),Serializable {

    private lateinit var binding: ActivityFolderBinding
    private lateinit var adapter: MusicListAdapter
    private lateinit var musicList :ArrayList<ModelAudio>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // status bar text color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        }

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = getColor(R.color.folderActivity)
            window.navigationBarColor = getColor(R.color.white)

        }

        val folder = intent.getSerializableExtra("folder") as Folder
        musicList = ArrayList()
        musicList = folder.musicList
        binding.textView.text = "${musicList.size} tracks"

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onQueryTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

            adapter =MusicListAdapter(this){  position: Int ->

            val intent = Intent(this, MusicActivity::class.java)
            intent.putExtra("musics", musicList)
            intent.putExtra("pos", position)
            startActivity(intent)
        }

        binding.recyclerView.setHasFixedSize(true)

        adapter.differ.submitList(musicList)
        binding.recyclerView.adapter = adapter

        binding.btnArrow.setOnClickListener {
            onBackPressed()
        }
    }

     fun onQueryTextChange(newText: String){
        val folder = intent.getSerializableExtra("folder") as Folder
        val userInput = newText.toLowerCase(Locale.ROOT)
        val myFiles = ArrayList<ModelAudio>()
        for (song in folder.musicList) {
            if (song.audioTitle!!.toLowerCase(Locale.ROOT).contains(userInput)){
                myFiles.add(song)
            }
        }
         musicList = myFiles
         adapter.differ.submitList(myFiles)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}