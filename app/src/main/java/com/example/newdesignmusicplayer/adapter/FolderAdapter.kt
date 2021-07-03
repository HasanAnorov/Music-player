package com.example.newdesignmusicplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newdesignmusicplayer.databinding.ViewPagerItemViewBinding
import com.example.newdesignmusicplayer.model.Folder


class FolderViewPagerAdapter(val itemClick:(folder: Folder) ->Unit): RecyclerView.Adapter<FolderViewPagerAdapter.ViewHolderHomeFragment>() {

    private val itemCallback = object : DiffUtil.ItemCallback<Folder>(){
        override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return  oldItem.folderName == newItem.folderName
        }


    }

    val differ = AsyncListDiffer(this,itemCallback)

    inner class ViewHolderHomeFragment(private  var binding:ViewPagerItemViewBinding): RecyclerView.ViewHolder(binding.root){
        fun onBind(model: Folder, position: Int){
            binding.folderName.text = model.folderName

//            model.musicPhoto.let {
//                Picasso.get().load(it)
//                        .fit()
//                        .centerCrop()
//                        .into(binding.ivPhoto)
//            }
            binding.root.setOnClickListener {
                itemClick.invoke(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHomeFragment {
        return  ViewHolderHomeFragment(
                ViewPagerItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolderHomeFragment, position: Int) {
        holder.onBind(differ.currentList[position],position)
    }
}