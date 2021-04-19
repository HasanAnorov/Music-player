package com.example.newdesignmusicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.newdesignmusicplayer.adapter.FolderViewPagerAdapter
import com.example.newdesignmusicplayer.adapter.MusicListAdapter
import com.example.newdesignmusicplayer.databinding.ActivityMainBinding
import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.model.ModelAudio
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.Serializable
import java.util.ArrayList
import kotlin.math.abs

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var audioArrayList: ArrayList<ModelAudio>

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        checkPermissions()
        audioArrayList = arrayListOf()

        //fetch the audio files from storage
        val contentResolver = this.contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = contentResolver?.query(uri, null, null, null, null)

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val duration: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val url: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val modelAudio = ModelAudio(title,duration,artist, Uri.parse(url))
//                modelAudio.setaudioTitle(title)
//                modelAudio.setaudioArtist(artist)
//                modelAudio.setaudioUri(Uri.parse(url))
//                modelAudio.setaudioDuration(duration)
                audioArrayList.add(modelAudio)
            } while (cursor.moveToNext())
        }

        val adapter = FolderViewPagerAdapter{ model: Folder, position: Int ->
            val intent = Intent(this,FolderActivity::class.java)
            //intent.putExtra("position",position)
            intent.putExtra("musics", audioArrayList)
            startActivity(intent)
            //Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
        }

        adapter.differ.submitList(mutableListOf(Folder(R.drawable.ic_thunder,"All songs"),
                Folder(R.drawable.ic_thunder,"All songs"),
                Folder(R.drawable.ic_thunder,"All songs"),
                Folder(R.drawable.ic_thunder,"All songs")))

        binding.viewPager.adapter=adapter

        binding.viewPager.clipToPadding = false
        binding.viewPager.clipChildren = false
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(ViewPager2.PageTransformer { page, position ->

            val r  = 1- abs(position)
            page.scaleY = 0.85f + r*0.15f
        })

        binding.viewPager.setPageTransformer(compositePageTransformer)

    }

    //checking permission
    private fun checkPermissions() {
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    }
                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                    override fun onPermissionRationaleShouldBeShown(
                            permissionRequest: PermissionRequest,
                            permissionToken: PermissionToken
                    ) {
                        // asking for permission
                        permissionToken.continuePermissionRequest()
                    }
                }).check()
    }

}