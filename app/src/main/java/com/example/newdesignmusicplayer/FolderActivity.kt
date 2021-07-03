  package com.example.newdesignmusicplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.newdesignmusicplayer.adapter.MusicListAdapter
import com.example.newdesignmusicplayer.databinding.ActivityFolderBinding
import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.model.ModelAudio
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class FolderActivity : AppCompatActivity(),Serializable,Playable {

    private lateinit var binding: ActivityFolderBinding
    private lateinit var adapter: MusicListAdapter
    private lateinit var musicList :ArrayList<ModelAudio>

    var position = 0
    var isPLaying :Boolean = false

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

        //creating channel
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            createChannel()
        }

        val folder = intent.getSerializableExtra("folder") as Folder
        musicList = ArrayList()
        musicList = folder.musicList
        binding.textView.text = "${folder.musicList.size} tracks"

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onQueryTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

            adapter =MusicListAdapter(this){ model: ModelAudio, position: Int ->

                musicList[position].isPlaying = true
                adapter.notifyItemChanged(position)

                CreateNotification().createNotification(this,model,R.drawable.ic_play_button_arrowhead,position,musicList.size-1)

            val intent = Intent(this, MusicActivity::class.java)
            intent.putExtra("musics", musicList)
            intent.putExtra("pos", position)
            startActivity(intent)
        }

        binding.recyclerView.setHasFixedSize(true)

        adapter.differ.submitList(musicList)
        binding.recyclerView.adapter = adapter

        binding.btnArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(CreateNotification().CHANNEL_ID,"Hasan",
            NotificationManager.IMPORTANCE_LOW)
        val notificationManager : NotificationManager =getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
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
         musicList = ArrayList()
         musicList = myFiles
        adapter.differ.submitList(myFiles)

    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.extras?.getString("actionname")
            when(action){
               CreateNotification().ACTION_PREVIOUS -> {
                   onTrackPrevious()
               }
               CreateNotification().ACTION_PLAY ->{
                    if (isPLaying){
                        onTrackPause()
                    }else{
                        onTrackPLay()
                    }
                }
               CreateNotification().ACTION_NEXT ->{
                   onTrackNext()
               }
            }
        }
    }

    override fun onTrackPrevious() {
        position--
        CreateNotification().createNotification(this,musicList[position],R.drawable.ic_pause,position,musicList.size-1)
        title
    }


    override fun onTrackPLay() {
        TODO("Not yet implemented")
    }

    override fun onTrackPause() {
        TODO("Not yet implemented")
    }

    override fun onTrackNext() {
        TODO("Not yet implemented")
    }

}



