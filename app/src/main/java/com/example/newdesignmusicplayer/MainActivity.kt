package com.example.newdesignmusicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.newdesignmusicplayer.adapter.FolderViewPagerAdapter
import com.example.newdesignmusicplayer.databinding.ActivityMainBinding
import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.model.ModelAudio
import com.example.newdesignmusicplayer.utils.Constants
import com.github.zawadz88.materialpopupmenu.popupMenu
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class MainActivity : AppCompatActivity(),OnFolderListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FolderViewPagerAdapter

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
            window.statusBarColor = getColor(R.color.main_light)
            window.navigationBarColor = getColor(R.color.white)
        }

        //checking permission
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.STORAGE_PERMISSION_CODE)
        } else {
            Constants.setFolders(Folder("You musics",Constants.fetchAudioFiles(this)))
            Constants.setFolders(Folder("Favorites", arrayListOf<ModelAudio>()))
        }

        //opening clicked playlist
        adapter = FolderViewPagerAdapter(this){ model: Folder ->
            val intent = Intent(this, FolderActivity::class.java)
            intent.putExtra("folder", model)
            startActivity(intent)
        }

        adapter.differ.submitList(Constants.getFolders())
        binding.recyclerView.adapter=adapter

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
                    Constants.setFolders(Folder(folderName, arrayListOf()))
                    dialog.dismiss()
                }
            }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                adapter = FolderViewPagerAdapter(this){ model: Folder ->
                    val intent = Intent(this, FolderActivity::class.java)
                    intent.putExtra("folder", model)
                    startActivity(intent)
                }
                Constants.setFolders(Folder("Your musics",Constants.fetchAudioFiles(this)))
                Constants.setFolders(Folder("Favorites", arrayListOf<ModelAudio>()))
                adapter.differ.submitList(Constants.getFolders())
                binding.recyclerView.adapter=adapter

                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFolderItemClick(view:View,folder:Folder,position: Int) {
        val popupMenu = popupMenu {
            style = R.style.Widget_MPM_Menu_Dark_CustomBackground
            section {
                item {
                    label = "Rename"
                    labelColor = ContextCompat.getColor(this@MainActivity,R.color.folderActivity)
                    icon = R.drawable.ic_edit__2_ //optional
                    iconColor = ContextCompat.getColor(this@MainActivity,R.color.folderActivity)
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
                            }else{
                                folder.folderName = folderName
                                adapter.notifyDataSetChanged()
                                dialog.dismiss()
                            }
                        }
                    }
                }
                item {
                    labelRes = R.string.remove
                    labelColor = ContextCompat.getColor(this@MainActivity,R.color.folderActivity)
                    iconDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_trash) //optional
                    iconColor =ContextCompat.getColor(this@MainActivity,R.color.folderActivity)
                    callback = {
                        //Do something to remove folder!
                        Toast.makeText(this@MainActivity, "Do something to remove folder!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        popupMenu.show(this, view)
    }
}