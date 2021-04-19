package com.example.newdesignmusicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newdesignmusicplayer.databinding.ActivityMusicBinding

class MusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}