package com.example.newdesignmusicplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newdesignmusicplayer.OnFolderListener
import com.example.newdesignmusicplayer.databinding.FolderItemViewBinding
import com.example.newdesignmusicplayer.room.RoomFolderModel
import java.io.Serializable


class FolderViewPagerAdapter(val listener:OnFolderListener,val itemClick:(folder: RoomFolderModel) ->Unit): RecyclerView.Adapter<FolderViewPagerAdapter.ViewHolderHomeFragment>(),Serializable {

    private val itemCallback = object : DiffUtil.ItemCallback<RoomFolderModel>(){
        override fun areItemsTheSame(oldItem: RoomFolderModel, newItem: RoomFolderModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RoomFolderModel, newItem: RoomFolderModel): Boolean {
            return  oldItem.folderName == newItem.folderName
        }


    }

    val differ = AsyncListDiffer(this,itemCallback)

    inner class ViewHolderHomeFragment(private  var binding:FolderItemViewBinding): RecyclerView.ViewHolder(binding.root){
        fun onBind(model: RoomFolderModel){
            binding.folderName.text = model.folderName
            binding.folderSongs.text = "${model.musicList.size} tracks"
            binding.cardMenu.elevation = 0F

            binding.root.setOnClickListener {
                itemClick.invoke(model)
            }
            if (model.folderName == "Your musics"||model.folderName=="Favorites"){
                binding.cardMenu.visibility = View.GONE
            }
            binding.cardMenu.setOnClickListener {
                listener.onFolderItemClick(binding.cardMenu,model,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHomeFragment {
        return  ViewHolderHomeFragment(
                FolderItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolderHomeFragment, position: Int) {
        holder.onBind(differ.currentList[position])
    }
}