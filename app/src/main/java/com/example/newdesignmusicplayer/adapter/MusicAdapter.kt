package com.example.newdesignmusicplayer.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newdesignmusicplayer.OnEvenListener
import com.example.newdesignmusicplayer.R
import com.example.newdesignmusicplayer.databinding.MusicItemViewBinding
import com.example.newdesignmusicplayer.room.RoomAudioModel

class MusicListAdapter(private val context:Context,val listener:OnEvenListener, val itemClick: ( pos: Int) -> Unit): RecyclerView.Adapter<MusicListAdapter.ViewHolderHomeFragment>() {

    private val itemCallback = object : DiffUtil.ItemCallback<RoomAudioModel>(){
        override fun areItemsTheSame(oldItem: RoomAudioModel, newItem: RoomAudioModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RoomAudioModel, newItem: RoomAudioModel): Boolean {
            return  oldItem.audioUri == newItem.audioUri
        }
    }

    var differ = AsyncListDiffer(this, itemCallback)

    inner class ViewHolderHomeFragment(private var binding: MusicItemViewBinding): RecyclerView.ViewHolder(binding.root){

         fun onBind(roomModel: RoomAudioModel, position: Int){

            binding.musicName.text = roomModel.audioTitle
            binding.musicAuthor.text = roomModel.audioArtist
            binding.cardMenu.elevation = 0F

            binding.cardMenu.setOnClickListener {
                listener.onMenuItemClick(roomModel,position,binding.cardMenu)
            }
            binding.root.setOnClickListener {
                itemClick.invoke( position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHomeFragment {
        return  ViewHolderHomeFragment(
                MusicItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolderHomeFragment, position: Int) {

            holder.onBind(differ.currentList[position], position)
            if (loadPhoto(position)!=null){
                Glide.with(context).asBitmap().load(loadPhoto(position)).into(holder.itemView.findViewById(R.id.onGoingMusicImage))
            }
    }

    private  fun loadPhoto(position: Int):ByteArray? = differ.currentList[position].audioUri?.let {
        getAlbumArt(it)
    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}