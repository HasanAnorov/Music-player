package com.example.newdesignmusicplayer.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
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

    var differ = AsyncListDiffer(this,itemCallback)

    inner class ViewHolderHomeFragment(private  var binding:MusicItemViewBinding): RecyclerView.ViewHolder(binding.root){
        fun onBind(model: ModelAudio, position: Int){
            binding.musicName.text = model.audioTitle
            binding.musicAuthor.text = model.audioArtist
            binding.musicTime.text = model.audioDuration?.let { timerConversion(it.toLong()) }
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

    //time conversion
    fun timerConversion(value: Long): String {
        val audioTime: String
        val dur = value.toInt()
        val hrs = dur / 3600000
        val mns = dur / 60000 % 60000
        val scs = dur % 60000 / 1000
        audioTime = if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mns, scs)
        } else {
            String.format("%02d:%02d", mns, scs)
        }
        return audioTime
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

//    fun updateList(searchMusicList:ArrayList<ModelAudio>){
//        differ = AsyncListDiffer(this,itemCallback)
//        differ.submitList(searchMusicList)
//        notifyDataSetChanged()
//    }

}