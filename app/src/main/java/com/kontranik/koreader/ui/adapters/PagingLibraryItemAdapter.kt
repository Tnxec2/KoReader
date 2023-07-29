package com.kontranik.koreader.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.database.model.LibraryItem
import com.kontranik.koreader.databinding.BooklistItemBinding
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import java.net.URLDecoder

class PagingLibraryItemAdapter(private val context: Context, private val mListener: PagingLibraryItemAdapterListener) :
    PagingDataAdapter<LibraryItem, PagingLibraryItemAdapter.LibraryItemVeiwHolder>(LibraryItemComparator()) {
    private var position: Int = 0

    interface  PagingLibraryItemAdapterListener {
        fun onClickLibraryItem(libraryItem: LibraryItem)
        fun onDeleteLibraryItem(position: Int, libraryItem: LibraryItem?)
        fun showUndoSnackbar(mRecentlyDeletedItem: LibraryItem?)
    }

    fun getPosition(): Int {
        return position
    }

    fun onSwipeDelete(position: Int){
        mListener.onDeleteLibraryItem(position, getItem(position))
    }

    fun cancelDeletion(position: Int){
        notifyItemChanged(position)
    }

    fun deleteItem(position: Int){
        notifyItemRemoved(position)
        mListener.showUndoSnackbar(getItem(position))
    }

    private fun setPosition(pos: Int) {
        this.position = pos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryItemVeiwHolder {
        return LibraryItemVeiwHolder(
            BooklistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LibraryItemVeiwHolder, position: Int) {
        setPosition(position)
        val item = getItem(position)
        if (item != null) {
            holder.binding.apply {
                booklistitemTitle.text = item.title
                booklistitemAuthor.text =  "" //item.authorsAsString()
                booklistitemPath.text = URLDecoder.decode(item.path)
                if (item.cover != null) {
                    val bitmap = ImageUtils.getImage(item.cover!!)
                    Log.d("LibraryAdapter", "${item.title} has bitmap")
                    if (bitmap != null)
                        booklistitemCover.setImageBitmap(bitmap)
                    else
                        booklistitemCover.setImageBitmap(ImageUtils.getBitmap(context, ImageEnum.Ebook))
                } else {
                    booklistitemCover.setImageBitmap(ImageUtils.getBitmap(context, ImageEnum.Ebook))
                }
            }
            holder.itemView.setOnClickListener { mListener.onClickLibraryItem(item) }
        }
    }

    override fun onViewRecycled(holder: LibraryItemVeiwHolder) {
        holder.itemView.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    class LibraryItemVeiwHolder(val binding: BooklistItemBinding) : RecyclerView.ViewHolder(binding.root)

    class LibraryItemComparator : DiffUtil.ItemCallback<LibraryItem>() {
        override fun areItemsTheSame(oldItem: LibraryItem, newItem: LibraryItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: LibraryItem, newItem: LibraryItem): Boolean =
            oldItem == newItem
    }
}