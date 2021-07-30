package com.example.newdesignmusicplayer.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.newdesignmusicplayer.R
import com.example.newdesignmusicplayer.adapter.MusicAdapter
import com.example.newdesignmusicplayer.databinding.ActivityFolderBinding
import com.example.newdesignmusicplayer.interfaces.OnMusicItemClick
import com.example.newdesignmusicplayer.room.RoomAudioModel
import com.example.newdesignmusicplayer.room.RoomDbHelper
import com.example.newdesignmusicplayer.viewmodel.MediaViewModel
import com.github.zawadz88.materialpopupmenu.popupMenu
import java.io.Serializable
import java.util.*

class FolderActivity : AppCompatActivity(),Serializable, OnMusicItemClick {

    private lateinit var binding: ActivityFolderBinding
    private lateinit var adapter: MusicAdapter
    private lateinit var musicList: List<RoomAudioModel>
    var isSelectionModeEnabled = false
    var selectList = ArrayList<RoomAudioModel>()
    private lateinit var viewModel:MediaViewModel
    private lateinit var folderName :String

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        //dbHelper = RoomDbHelper.DatabaseBuilder.getInstance(this)
        viewModel = ViewModelProvider(this).get(MediaViewModel::class.java)

        // status bar text color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = getColor(R.color.main_light)
            window.navigationBarColor = getColor(R.color.white)
        }

        folderName = intent.getStringExtra("folderName") as String
        viewModel.getFolder(folderName).observe(this){
            musicList = it.audioList
            binding.textView.text = "${musicList.size} tracks"
            setAdapter(musicList, folderName)
        }

        binding.tvFolderName.text = folderName
        binding.btnArrow.elevation = 0F
        binding.addCard.elevation = 0F
        binding.shareCard.elevation = 0F
        binding.deleteCard.elevation = 0F
        binding.selectCard.elevation = 0F
        binding.selectCard.elevation = 0F

        binding.addCard.setOnClickListener {
            for(i in 0 until selectList.size){
                selectList[i].isSelected = false
            }
            val intent = Intent(this, FolderSelectionActivity::class.java)
            intent.putExtra("data", selectList as Serializable)
            startActivity(intent)

            binding.bottomSheet.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.navigationBarColor = getColor(R.color.white)
            }
            setBehaviour(false)
        }

        binding.shareCard.setOnClickListener {
            Toast.makeText(this, selectList[0].audioUri, Toast.LENGTH_SHORT).show()
            val sharePath: String = Environment.getExternalStorageDirectory().path
                    .toString() + Uri.parse(selectList[0].audioUri)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            val shareBody =  Uri.parse(selectList[0].audioUri)
            sharingIntent.putExtra(Intent.EXTRA_STREAM, shareBody)
            sharingIntent.type = "audio/*"
            startActivity(Intent.createChooser(sharingIntent, "Share using"))
            binding.bottomSheet.visibility = View.GONE
            setBehaviour(false)
        }

        binding.deleteCard.setOnClickListener {
            viewModel.getFolder(folderName).observe(this){
                val folderList = it.audioList.toMutableList()
                for(i in 0 until selectList.size){
                    selectList[i].isSelected = false
                    folderList.remove(selectList[i])
                    Toast.makeText(this, selectList[i].audioTitle, Toast.LENGTH_SHORT).show()
                }
                it.audioList = folderList
                viewModel.updateFolder(it)
            }

            
            binding.bottomSheet.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.navigationBarColor = getColor(R.color.white)
            }
            setBehaviour(false)
        }

        binding.selectCard.setOnClickListener {
            if (selectList.size == adapter.differ.currentList.size) {
                binding.selectAllIv.setImageResource(R.drawable.ic_dry_clean)
                binding.selectedMusicCount.text = "0"
                //isSelectionModeEnabled = false
                for (i in 0 until adapter.differ.currentList.size){
                    adapter.differ.currentList[i].isSelected = false
                }
                adapter.notifyDataSetChanged()
                //clear select array list
                selectList.clear()
            } else {
                //when all item unselected
                //set isSelectAll true
                isSelectionModeEnabled = true
                binding.selectAllIv.setImageResource(R.drawable.ic_check__2_)
                //clear select array list
                selectList.clear()
                //add all value to select array list
                selectList.addAll(adapter.differ.currentList)
                for (i in 0 until adapter.differ.currentList.size){
                    adapter.differ.currentList[i].isSelected = true
                }
                adapter.notifyDataSetChanged()
                binding.selectedMusicCount.text = adapter.differ.currentList.size.toString()
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onQueryTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.btnArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setAdapter(musicList: List<RoomAudioModel>, folderNameMusic: String) {
        adapter = MusicAdapter(this, this) { position: Int ->

            if (isSelectionModeEnabled) {
                onItemCLickForSelection(position)
            } else {
                val intent = Intent(this, MusicActivity::class.java)
                intent.putExtra("folderName", folderNameMusic)
                intent.putExtra("position", position)
                startActivity(intent)
            }
        }
        adapter.differ.submitList(musicList)
        binding.recyclerView.adapter = adapter
    }

    fun onQueryTextChange(newText: String) {
        val userInput = newText.toLowerCase(Locale.ROOT)
        val myFiles = arrayListOf<RoomAudioModel>()
        for (song in musicList) {
            if (song.audioTitle.toLowerCase(Locale.ROOT).contains(userInput)) {
                myFiles.add(song)
            }
        }
        //adapter.differ.submitList(myFiles)
        setAdapter(myFiles,folderName)
    }

    override fun onMenuItemClick(model: RoomAudioModel, position: Int, view: View) {

        val popupMenu = popupMenu {
            style = R.style.Widget_MPM_Menu_Dark_CustomBackground
            section {
                item {
                    label = "Add to"
                    labelColor = ContextCompat.getColor(this@FolderActivity, R.color.folderActivity)
                    icon = R.drawable.ic_add__4_ //optional
                    iconColor = ContextCompat.getColor(this@FolderActivity, R.color.folderActivity)
                    callback = {
                        val intent = Intent(this@FolderActivity, FolderSelectionActivity::class.java)
                        val data = listOf<RoomAudioModel>(model)
                        intent.putExtra("data", data as Serializable)
                        startActivity(intent)
                    }
                }
                if (folderName.toString()!="Your musics"){
                    item {
                        labelRes = R.string.remove
                        labelColor = ContextCompat.getColor(this@FolderActivity, R.color.folderActivity)
                        iconDrawable = ContextCompat.getDrawable(this@FolderActivity, R.drawable.ic_trash) //optional
                        iconColor = ContextCompat.getColor(this@FolderActivity, R.color.folderActivity)
                        callback = {
                            //Do something to remove folder!
                            Toast.makeText(this@FolderActivity, "Do something to remove folder!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        popupMenu.show(this, view)
    }

    override fun onBackPressed() {
        if (isSelectionModeEnabled) {
            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
            isSelectionModeEnabled = false
            for(i in 0 until adapter.differ.currentList.size){
                adapter.differ.currentList[i].isSelected = false
            }
            adapter.notifyDataSetChanged()
            binding.bottomSheet.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.navigationBarColor = getColor(R.color.white)
            }
            binding.selectedMusicCount.text = 0.toString()
        } else{
            super.onBackPressed()
        }
        if (binding.bottomSheet.visibility == View.VISIBLE){
            Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
            binding.bottomSheet.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.navigationBarColor = getColor(R.color.white)
            }
            binding.selectedMusicCount.text = 0.toString()
        }
    }

    override fun onMusicModelLongClick(position: Int) {
        isSelectionModeEnabled = true
        onItemCLickForSelection(position)
        showBottomSheet()
    }

    private fun onItemCLickForSelection(position: Int) {
        val roomAudioModel = adapter.differ.currentList[position]
        adapter.differ.currentList[position].isSelected = !roomAudioModel.isSelected
        if (adapter.differ.currentList[position].isSelected) {
            binding.selectedMusicCount.text = "${binding.selectedMusicCount.text.toString().toInt() + 1}"
        } else {
            binding.selectedMusicCount.text = "${binding.selectedMusicCount.text.toString().toInt() - 1}"
        }
        selectList.add(roomAudioModel)
        adapter.notifyItemChanged(position)
    }

    private fun setBehaviour(boolean: Boolean){
        isSelectionModeEnabled = false
        for(i in 0 until adapter.differ.currentList.size){
            adapter.differ.currentList[i].isSelected = boolean
        }
        adapter.notifyDataSetChanged()
    }

    private fun showBottomSheet() {
        if (binding.bottomSheet.visibility == View.GONE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.navigationBarColor = getColor(R.color.main_light)
            }
            binding.bottomSheet.visibility = View.VISIBLE
            binding.bottomSheet.animation = AnimationUtils.loadAnimation(this, R.anim.form_bottom)
        }
    }
}