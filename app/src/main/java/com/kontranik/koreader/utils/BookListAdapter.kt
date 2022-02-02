package com.kontranik.koreader.utils

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.parser.epubreader.EpubHelper
import com.kontranik.koreader.parser.fb2reader.FB2Helper
import java.net.URLDecoder

class BookListAdapter(
        val context: Context,
        private val books: MutableList<BookInfo>,
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
        holder.pathView.text = URLDecoder.decode(bookInfo.path)
        if (bookInfo.cover != null) {
            holder.imageView.setImageBitmap(bookInfo.cover!!)
        } else {
            bookInfo.cover = ImageUtils.getBitmap(context, ImageEnum.Ebook)
            holder.imageView.setImageBitmap(bookInfo.cover)
            val asyncTask = ReadBookInfoAsync(this)
            asyncTask.execute(position)
        }

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


    class ReadBookInfoAsync(val adapter: BookListAdapter) : AsyncTask<Int?, Int?, BookInfo?>() {

        var position = 0
        override fun doInBackground(vararg params: Int?): BookInfo? {
            var result: BookInfo? = null
            if (params.isNotEmpty() && params.first() != null) {
                position = params.first()!!
                if ( position >= adapter.books.size ) return null
                result = adapter.books[position]
                val contentUriPath = result.path

                val mContext = adapter.context
                var bookInfo: BookInfo? = null
                if (contentUriPath.endsWith(".epub", ignoreCase = true)) {
                    bookInfo = EpubHelper(mContext, contentUriPath).getBookInfoTemporary(contentUriPath)
                } else if (contentUriPath.endsWith(".fb2", ignoreCase = true)
                    || contentUriPath.endsWith(".fb2.zip", ignoreCase = true)) {
                    bookInfo = FB2Helper(mContext, contentUriPath).getBookInfoTemporary(contentUriPath)
                }

                if ( bookInfo?.cover != null) {
                    result.cover = ImageUtils.scaleBitmap(bookInfo.cover!!, 50, 100)
                } else {
                    result.cover =
                        ImageUtils.getBitmap(adapter.context, ImageEnum.Ebook)
                }

            }
            return result
        }

        override fun onPostExecute(result: BookInfo?) {
            if ( result != null) adapter.notifyItemChanged(position)
        }
    }
}