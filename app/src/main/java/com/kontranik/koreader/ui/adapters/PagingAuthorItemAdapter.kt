package com.kontranik.koreader.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.databinding.AuthorsListItemBinding

class PagingAuthorItemAdapter(private val context: Context, private val mListener: PagingAuthorItemAdapterListener) :
    PagingDataAdapter<Author, PagingAuthorItemAdapter.LibraryItemVeiwHolder>(AuthorComparator()) {
    private var position: Int = 0

    interface  PagingAuthorItemAdapterListener {
        fun onClickAuthorItem(author: Author)
        fun onDeleteAuthorItem(position: Int, author: Author?)
    }

    fun getPosition(): Int {
        return position
    }

    fun onSwipeDelete(position: Int){
        mListener.onDeleteAuthorItem(position, getItem(position))
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
            AuthorsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LibraryItemVeiwHolder, position: Int) {
        setPosition(position)
        val author = getItem(position)
        if (author != null) {
            holder.binding.apply {
                authorListitemTitle.text = author.lastname
                authorListitemSubtitle.text = author.firstname
            }
            holder.itemView.setOnClickListener { mListener.onClickAuthorItem(author) }
        }
    }

    override fun onViewRecycled(holder: LibraryItemVeiwHolder) {
        holder.itemView.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    class LibraryItemVeiwHolder(val binding: AuthorsListItemBinding) : RecyclerView.ViewHolder(binding.root)

    class AuthorComparator : DiffUtil.ItemCallback<Author>() {
        override fun areItemsTheSame(oldItem: Author, newItem: Author): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Author, newItem: Author): Boolean =
            oldItem == newItem
    }
}