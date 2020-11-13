package com.kontranik.koreader.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.model.BookInfo

class BookListAdapter(
        val context: Context,
        private val books: List<BookInfo>,
        private val bookListAdapterClickListener: BookListAdapterClickListener) :
        RecyclerView.Adapter<BookListAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    interface BookListAdapterClickListener {
        fun onBooklistItemClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.booklist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookInfo: BookInfo = books[position]

        holder.titleView.text = bookInfo.title
        holder.authorView.text = bookInfo.authorsAsString()
        if (bookInfo.cover != null) {
            holder.imageView.setImageBitmap(ImageUtils.scaleBitmap(bookInfo.cover!!, 50, 100 ))
        } else {
            holder.imageView.setImageBitmap(ImageUtils.getBitmap(context, ImageEnum.Ebook))
        }
        holder.pathView.text = bookInfo.path

        holder.itemView.setOnClickListener {
            bookListAdapterClickListener.onBooklistItemClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<View>(R.id.booklistitem_cover) as ImageView
        val titleView = itemView.findViewById<View>(R.id.booklistitem_title) as TextView
        val authorView = itemView.findViewById<View>(R.id.booklistitem_author) as TextView
        val pathView = itemView.findViewById<View>(R.id.booklistitem_path) as TextView
    }
}