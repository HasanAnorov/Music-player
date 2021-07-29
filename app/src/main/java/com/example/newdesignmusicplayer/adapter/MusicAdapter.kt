package com.example.newdesignmusicplayer.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newdesignmusicplayer.interfaces.OnMusicItemClick
import com.example.newdesignmusicplayer.databinding.MusicItemViewBinding
import com.example.newdesignmusicplayer.room.RoomAudioModel


class MusicAdapter(
        private val context: Context,
        val listener: OnMusicItemClick,
        val itemClick: (pos: Int) -> Unit
): RecyclerView.Adapter<MusicAdapter.ViewHolderHomeFragment>() {

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

        fun onBind(model: RoomAudioModel, position: Int){

            val image = getAlbumArt(differ.currentList[position].audioUri)
            binding.musicName.text = model.audioTitle
            binding.musicAuthor.text = model.audioArtist
            binding.cardMenu.elevation = 0F
            binding.cardMusicPhoto.elevation = 0F

            if (image!=null){
                Glide.with(context).asBitmap().load(image).into(binding.onGoingMusicImage)
            }
            if (model.isSelected){
                binding.ivBack.visibility = View.GONE
                binding.ivSelector.visibility = View.VISIBLE
                binding.cardMenu.isEnabled = false
            }else{
                binding.ivBack.visibility = View.VISIBLE
                binding.ivSelector.visibility = View.GONE
                binding.cardMenu.isEnabled = true
            }

            binding.cardMenu.setOnClickListener {
                listener.onMenuItemClick(model, position, binding.cardMenu)
            }
            binding.root.setOnClickListener {
                    itemClick.invoke(position)
            }
            binding.root.setOnLongClickListener {
                listener.onMusicModelLongClick(position)

                return@setOnLongClickListener true
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
    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}