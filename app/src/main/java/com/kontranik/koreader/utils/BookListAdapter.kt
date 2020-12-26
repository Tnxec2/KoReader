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
import java.lang.ref.WeakReference
import java.net.URLDecoder

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
        holder.pathView.text = URLDecoder.decode(bookInfo.path)
        if (bookInfo.cover != null) {
            holder.imageView.setImageBitmap(ImageUtils.scaleBitmap(bookInfo.cover!!, 50, 100 ))
        } else {
            val asyncTask = ReadBookInfoAsync(context, holder)
            asyncTask.execute(bookInfo)
            holder.imageView.setImageBitmap(ImageUtils.getBitmap(context, ImageEnum.Ebook))
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


    class ReadBookInfoAsync(context: Context, val holder: ViewHolder) : AsyncTask<BookInfo?, Int?, BookInfo?>() {
        // Weak references will still allow the Activity to be garbage-collected
        private val weakContext: WeakReference<Context> = WeakReference(context)

        override fun doInBackground(vararg params: BookInfo?): BookInfo? {
            var result: BookInfo? = null
            if (params.isNotEmpty()) {
                result = params.first()
                val contentUriPath = result!!.path

                val mContext = weakContext.get()
                if (contentUriPath.endsWith(".epub", ignoreCase = true)) {
                    result = EpubHelper(mContext!!, contentUriPath).getBookInfoTemporary(contentUriPath)
                } else if (contentUriPath.endsWith(".fb2", ignoreCase = true)
                        || contentUriPath.endsWith(".fb2.zip", ignoreCase = true)) {
                    result = FB2Helper(mContext!!, contentUriPath).getBookInfoTemporary(contentUriPath)
                }
            }
            return result
        }

        protected fun onProgressUpdate() {
            //called when the background task makes any progress
        }

        override fun onPreExecute() {
            //called before doInBackground() is started
        }

        override fun onPostExecute(bookInfo: BookInfo?) {
            Log.d("ReadBookInfoAsync", bookInfo.toString())
            if ( bookInfo != null ) {
                holder.titleView.text = bookInfo.title
                holder.authorView.text = bookInfo.authorsAsString()
                if (bookInfo.cover != null) {
                    bookInfo.cover = ImageUtils.scaleBitmap(bookInfo.cover!!, 50, 100)
                } else {
                    bookInfo.cover = ImageUtils.getBitmap(weakContext.get()!!, ImageEnum.Ebook)
                }
                holder.imageView.setImageBitmap(bookInfo.cover!!)
            }
        }

    }
}