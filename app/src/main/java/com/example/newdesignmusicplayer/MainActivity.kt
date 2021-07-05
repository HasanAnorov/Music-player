package com.example.newdesignmusicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.newdesignmusicplayer.adapter.FolderViewPagerAdapter
import com.example.newdesignmusicplayer.databinding.ActivityMainBinding
import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.model.ModelAudio
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var audioArrayList: ArrayList<ModelAudio>

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // status bar text color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or  View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        }

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = getColor(R.color.white)
            window.navigationBarColor = getColor(R.color.white)
        }

        binding.cardMenu.elevation = 0F

        //checking permission
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            //ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
            requestStoragePermission()
        } else {
            audioArrayList = arrayListOf()

            //fetch the audio files from storage
            val contentResolver = this.contentResolver
            val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val cursor: Cursor? = contentResolver?.query(uri, null, null, null, null)

            //looping through all rows and adding to list
            when{
                cursor == null -> {
                    // query failed, handle error.
                    Toast.makeText(this, "Cannot read music", Toast.LENGTH_SHORT).show()
                }
                !cursor.moveToFirst() -> {
                    // no media on the device
                    Toast.makeText(this, "No music found on this phone", Toast.LENGTH_SHORT).show()
                }
                else ->{
                    //if(cursor.moveToFirst()) {
                    do {
                        val id: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                        val title: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                        val artist: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                        val url: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                        audioArrayList.add(ModelAudio(id, title, "null", artist, url, false, false))
                    } while (cursor.moveToNext())
                    // }
                    cursor.close()
                }
            }
        }

        //opening clicked playlist
        val adapter = FolderViewPagerAdapter{ model: Folder ->
            val intent = Intent(this, FolderActivity::class.java)
            intent.putExtra("folder", model)
            startActivity(intent)
        }

        adapter.differ.submitList(mutableListOf(
                Folder(R.drawable.ic_thunder, "All songs", audioArrayList),
                Folder(R.drawable.ic_star, "Favorites", audioArrayList)))
        adapter.notifyDataSetChanged()

        binding.viewPager.adapter=adapter

        binding.viewPager.clipToPadding = false
        binding.viewPager.clipChildren = false
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        binding.viewPager.setPageTransformer(compositePageTransformer)
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to read your musics from external storage")
                    .setPositiveButton("ok") { dialog, which -> ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE) }
                    .setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
                    .create().show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}