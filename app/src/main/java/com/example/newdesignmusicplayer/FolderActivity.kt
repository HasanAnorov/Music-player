package com.example.newdesignmusicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newdesignmusicplayer.adapter.MusicListAdapter
import com.example.newdesignmusicplayer.databinding.ActivityFolderBinding
import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.model.ModelAudio
import java.io.Serializable

class FolderActivity : AppCompatActivity(),Serializable {

    private lateinit var binding: ActivityFolderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        var musicList = intent.getSerializableExtra("musics") as List<ModelAudio>
        //var position = intent.getIntExtra("position",0)

        val adapter =MusicListAdapter{ model: ModelAudio, position: Int ->

        }
        //adapter.differ.submitList(musicList)
        //binding.recyclerView.adapter = adapter

    }
}