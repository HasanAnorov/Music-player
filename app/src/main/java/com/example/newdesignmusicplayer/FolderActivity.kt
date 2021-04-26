package com.example.newdesignmusicplayer

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Display
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newdesignmusicplayer.adapter.MusicListAdapter
import com.example.newdesignmusicplayer.databinding.ActivityFolderBinding
import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.model.ModelAudio
import java.io.Serializable
import kotlin.collections.ArrayList

class FolderActivity : AppCompatActivity(),Serializable {

    private lateinit var binding: ActivityFolderBinding
    private lateinit var adapter: MusicListAdapter
    private lateinit var musicList :ArrayList<ModelAudio>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val folder = intent.getSerializableExtra("folder") as Folder
        musicList = ArrayList()
        musicList = folder.musicList

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onQueryTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.musicName.text = folder.musicList[0].audioTitle
        binding.musicAuthor.text = folder.musicList[0].audioArtist

            adapter =MusicListAdapter{ model: ModelAudio, position: Int ->
            val intent = Intent(this, MusicActivity::class.java)
            intent.putExtra("musics", musicList)
            intent.putExtra("pos", position)
            startActivity(intent)

            binding.musicAuthor.text = model.audioArtist
            binding.musicName.text = model.audioTitle

        }

        binding.cardPausePlay.setOnClickListener {

        }

        adapter.differ.submitList(folder.musicList)
        binding.recyclerView.adapter = adapter

        binding.btnArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

     fun onQueryTextChange(newText: String){
        val folder = intent.getSerializableExtra("folder") as Folder
        val userInput = newText.toLowerCase()
        val myFiles = ArrayList<ModelAudio>()
        for (song in folder.musicList) {
            if (song.audioTitle!!.toLowerCase().contains(userInput)){
                myFiles.add(song)
            }
        }
         musicList = ArrayList()
         musicList = myFiles
        adapter.differ.submitList(myFiles)

    }

}



