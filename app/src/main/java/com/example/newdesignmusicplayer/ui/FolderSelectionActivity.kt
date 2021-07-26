package com.example.newdesignmusicplayer.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.newdesignmusicplayer.R
import com.example.newdesignmusicplayer.adapter.AdapterForFolderSelection
import com.example.newdesignmusicplayer.databinding.ActivityFolderSelectionBinding
import com.example.newdesignmusicplayer.interfaces.OnFolderForSelection
import com.example.newdesignmusicplayer.room.RoomAudioModel
import com.example.newdesignmusicplayer.room.RoomDbHelper
import com.example.newdesignmusicplayer.room.RoomFolderModel
import java.io.Serializable

class FolderSelectionActivity : AppCompatActivity(), OnFolderForSelection,Serializable {

    private lateinit var binding:ActivityFolderSelectionBinding
    private lateinit var dbHelper: RoomDbHelper
    private lateinit var adapter :AdapterForFolderSelection
    private lateinit var data : List<RoomAudioModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        dbHelper = RoomDbHelper.DatabaseBuilder.getInstance(this)
        adapter = AdapterForFolderSelection(this)
        data = intent.getSerializableExtra("data") as List<RoomAudioModel>

        // status bar text color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = getColor(R.color.main_light)
            window.navigationBarColor = getColor(R.color.white)
        }

        binding.btnArrow.elevation = 0F
        binding.addFolder.elevation = 0F

        binding.btnArrow.setOnClickListener {
            onBackPressed()
        }

        binding.addFolder.setOnClickListener {
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
                    //do mentioned
                    val newRoomFolder = RoomFolderModel(folderName = folderName,audioList = listOf<RoomAudioModel>())
                    dbHelper.roomDao().insertFolder(newRoomFolder)
                    adapter.notifyItemInserted(dbHelper.roomDao().getFoldersCount()-1)
                    //adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
        }
        dbHelper.roomDao().getFolders()?.let { setAdapter(it) }

    }

    private  fun setAdapter(folders:List<RoomFolderModel>){
        adapter.differ.submitList(folders)
        binding.recyclerView.adapter=adapter
    }

    override fun onFolderForSelectionClick(model: RoomFolderModel, position: Int) {
        val folder = dbHelper.roomDao().getFolder(model.folderName)
        val wholeData  = ArrayList<RoomAudioModel>()
        val nonDuplicatedMusic = ArrayList<RoomAudioModel>()

        for (i in data.indices){
            if (!folder.audioList.contains(data[i])){
                nonDuplicatedMusic.add(data[i])
            }else{
                Toast.makeText(this, "found   ", Toast.LENGTH_SHORT).show()
            }
        }
        wholeData.addAll(folder.audioList)
        wholeData.addAll(nonDuplicatedMusic)
        folder.audioList = wholeData
        dbHelper.roomDao().updateFolder(folder)
        onBackPressed()
    }
}