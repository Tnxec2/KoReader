package com.kontranik.koreader.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import com.kontranik.koreader.databinding.BooklistItemBinding
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import java.net.URLDecoder

class PagingLibraryItemAdapter(private val context: Context, private val mListener: PagingLibraryItemAdapterListener) :
    PagingDataAdapter<LibraryItemWithAuthors, PagingLibraryItemAdapter.LibraryItemVeiwHolder>(LibraryItemComparator()) {
    private var position: Int = 0

    interface  PagingLibraryItemAdapterListener {
        fun onClickLibraryItem(libraryItem: LibraryItemWithAuthors)
        fun onDeleteLibraryItem(position: Int, libraryItem: LibraryItemWithAuthors?)
        fun onUpdateLibraryItem(position: Int, libraryItem: LibraryItemWithAuthors?)
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
        // mListener.showUndoSnackbar(getItem(position))
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
        val libraryItemWithAuthors = getItem(position)
        if (libraryItemWithAuthors != null) {
            holder.binding.apply {
                booklistitemTitle.text = libraryItemWithAuthors.libraryItem.title
                booklistitemAuthor.text =  authorsAsString(libraryItemWithAuthors.authors)
                booklistitemPath.text = URLDecoder.decode(libraryItemWithAuthors.libraryItem.path)
                if (libraryItemWithAuthors.libraryItem.cover != null) {
                    val bitmap = ImageUtils.getImage(libraryItemWithAuthors.libraryItem.cover!!)
                    if (bitmap != null)
                        booklistitemCover.setImageBitmap(bitmap)
                    else
                        booklistitemCover.setImageBitmap(ImageUtils.getBitmap(context, ImageEnum.Ebook))
                } else {
                    booklistitemCover.setImageBitmap(ImageUtils.getBitmap(context, ImageEnum.Ebook))
                }
            }
            holder.itemView.setOnClickListener { mListener.onClickLibraryItem(libraryItemWithAuthors) }

            holder.itemView.setOnLongClickListener {
                val popup = PopupMenu(context, holder.itemView)
                popup.inflate(R.menu.menu_library_item_clicked)
                popup.setOnMenuItemClickListener { menuItem: MenuItem? ->
                    if (menuItem != null) {
                        when (menuItem.itemId) {
                            R.id.itemDelete -> {
                                mListener.onDeleteLibraryItem(position, libraryItemWithAuthors)
                            }
                            R.id.itemUpdate -> {
                                mListener.onUpdateLibraryItem(position, libraryItemWithAuthors)
                            }
                        }
                    }
                    false
                }
                popup.show()
                true
            }
        }
    }

    fun authorsAsString(authors: List<Author>): String {
        var result = ""
        for (author in authors) {
            if (result.isNotEmpty()) result += ", "
            result += author.firstname + " " + author.lastname
        }

        return result
    }

    override fun onViewRecycled(holder: LibraryItemVeiwHolder) {
        with(holder.itemView) {
            setOnClickListener(null)
            setOnLongClickListener(null)
        }

        super.onViewRecycled(holder)
    }

    class LibraryItemVeiwHolder(val binding: BooklistItemBinding) : RecyclerView.ViewHolder(binding.root)

    class LibraryItemComparator : DiffUtil.ItemCallback<LibraryItemWithAuthors>() {
        override fun areItemsTheSame(oldItem: LibraryItemWithAuthors, newItem: LibraryItemWithAuthors): Boolean =
            oldItem.libraryItem.id == newItem.libraryItem.id

        override fun areContentsTheSame(oldItem: LibraryItemWithAuthors, newItem: LibraryItemWithAuthors): Boolean =
            oldItem == newItem
    }
}