package com.example.newdesignmusicplayer.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newdesignmusicplayer.databinding.MusicItemViewBinding
import com.example.newdesignmusicplayer.model.Folder
import com.example.newdesignmusicplayer.model.ModelAudio


class MusicListAdapter(val itemClick:(music: ModelAudio, pos:Int) ->Unit): RecyclerView.Adapter<MusicListAdapter.ViewHolderHomeFragment>() {

    private val itemCallback = object : DiffUtil.ItemCallback<ModelAudio>(){
        override fun areItemsTheSame(oldItem: ModelAudio, newItem: ModelAudio): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ModelAudio, newItem: ModelAudio): Boolean {
            return  oldItem.audioUri == newItem.audioUri
        }


    }

    val differ = AsyncListDiffer(this,itemCallback)

    inner class ViewHolderHomeFragment(private  var binding:MusicItemViewBinding): RecyclerView.ViewHolder(binding.root){
        fun onBind(model: ModelAudio, position: Int){
            binding.musicName.text = model.audioTitle
            binding.musicAuthor.text = model.audioArtist
            binding.musicTime.text = model.audioDuration
//            model.musicPhoto.let {
//                Picasso.get().load(it)
//                        .fit()
//                        .centerCrop()
//                        .into(binding.ivPhoto)
//            }
            binding.root.setOnClickListener {
                itemClick.invoke(model,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHomeFragment {
        return  ViewHolderHomeFragment(
            MusicItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolderHomeFragment, position: Int) {
        holder.onBind(differ.currentList[position],position)
    }

}