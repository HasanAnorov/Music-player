package com.example.newdesignmusicplayer.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newdesignmusicplayer.OnMusicItemClick
import com.example.newdesignmusicplayer.R
import com.example.newdesignmusicplayer.databinding.MusicItemViewBinding
import com.example.newdesignmusicplayer.room.RoomAudioModel


class MusicListAdapter(
        private val context: Context,
        val listener: OnMusicItemClick,
        val itemClick: (pos: Int) -> Unit
): RecyclerView.Adapter<MusicListAdapter.ViewHolderHomeFragment>() {

    private val itemCallback = object : DiffUtil.ItemCallback<RoomAudioModel>(){
        override fun areItemsTheSame(oldItem: RoomAudioModel, newItem: RoomAudioModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RoomAudioModel, newItem: RoomAudioModel): Boolean {
            return  oldItem.audioUri == newItem.audioUri
        }
    }

    var differ = AsyncListDiffer(this, itemCallback)
//    var isEnable = false
//    var isSelectAll = false
//    var selectList = ArrayList<RoomAudioModel>()

    inner class ViewHolderHomeFragment(private var binding: MusicItemViewBinding): RecyclerView.ViewHolder(
            binding.root){
        fun onBind(model: RoomAudioModel, position: Int){

            val image = getAlbumArt(differ.currentList[position].audioUri)
            binding.musicName.text = model.audioTitle
            binding.musicAuthor.text = model.audioArtist
            binding.cardMenu.elevation = 0F
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
                binding.cardMenu.isEnabled = false
            }

//            if (isSelectAll){
//                //when all value selected
//                //visible all checkbox image
//                binding.ivBack.visibility = View.GONE
//                binding.ivSelector.visibility = View.VISIBLE
//                binding.cardMenu.isEnabled = false
//            }else{
//                //when all value un selected
//                //hide all checkbox image
//                binding.cardMenu.isEnabled = true
//                binding.ivSelector.visibility = View.GONE
//                binding.ivBack.visibility = View.VISIBLE
//            }

            binding.cardMenu.setOnClickListener {
                listener.onMenuItemClick(model, position, binding.cardMenu)
            }
            binding.root.setOnClickListener {
//                if (isEnable){
//                    //when action mode is enable
//                    //call method
//                    onItemClickForSelection(position)
//                }else{
                    itemClick.invoke(position)
                //}
            }
            binding.root.setOnLongClickListener {
                listener.onMusicModelLongClick(position)
//                if (!isEnable){
//                    //when action mode isn't enabled
//                    //Initializing action mode
//                    val callback = object : ActionMode.Callback{
//                        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
//                            //Initializing menu inflater
//                            val menuInflater = mode?.menuInflater
//                            menuInflater?.inflate(R.menu.selector_menu, menu)
//                            return true
//                        }
//
//                        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
//                            //When action mode is prepare
//                            //Set is enable true
//                            isEnable = true
//                            //create method
//                            onItemClickForSelection(position)
//                            return true
//                        }
//
//                        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
//                            //when click on action mode item
//                            //get item id
//                            when(item?.itemId){
//                                R.id.menu_delete -> {
//                                    for (s in selectList) {
//                                        differ.currentList.remove(s)
//                                    }
//                                    //finish action mode
//                                    mode?.finish()
//                                }
//                                R.id.menu_select_all -> {
//                                    //when click on select all
//                                    //check condition
//                                    if (selectList.size == differ.currentList.size) {
//                                        isSelectAll = false
//                                        //clear select array list
//                                        selectList.clear()
//                                    } else {
//                                        //when all item unselected
//                                        //set isSelectAll true
//                                        isSelectAll = true
//                                        //clear select array list
//                                        selectList.clear()
//                                        //add all value to select array list
//                                        selectList.addAll(differ.currentList)
//                                    }
//                                    for (i in 0 until differ.currentList.size){
//                                        notifyItemChanged(i,differ.currentList[i])
//                                    }
//                                }
//                            }
//                            return true
//                        }
//
//                        override fun onDestroyActionMode(mode: ActionMode?) {
//                            //when action mode is destroy
//                            //set isEnable false
//                            isEnable  =false
//                            isSelectAll = false
//                            selectList.clear()
//                            for (i in 0 until differ.currentList.size){
//                                notifyItemChanged(i,differ.currentList[i])
//                            }
//                        }
//                    }
//                    ((context as AppCompatActivity)).startActionMode(callback)
//                }else{
//                    onItemClickForSelection(position)
//                }
                return@setOnLongClickListener true
            }
        }

//        fun onItemClickForSelection(position: Int){
//            val selectedMusic = differ.currentList[position]
//            //check condition
//            if (binding.ivSelector.visibility  == View.GONE){
//                //when item not selected
//                //Visible check box image
//                binding.cardMenu.isEnabled = false
//                binding.ivBack.visibility = View.GONE
//                binding.ivSelector.visibility = View.VISIBLE
//                isSelectAll = false
//                //for (i in 0 until differ.currentList.size){
//                    notifyItemChanged(position,differ.currentList[position])
//                //}
//                //add value to selectList
//                selectList.add(selectedMusic)
//            }else{
//                //when item selected
//                binding.cardMenu.isEnabled = true
//                binding.ivBack.visibility = View.VISIBLE
//                binding.ivSelector.visibility = View.GONE
//                isSelectAll = true
//                //for (i in 0 until differ.currentList.size){
//                notifyItemChanged(position,differ.currentList[position])
//                //}
//                //remove value from selectList
//                selectList.remove(selectedMusic)
//            }
//        }
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