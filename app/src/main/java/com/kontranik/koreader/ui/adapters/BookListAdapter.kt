package com.kontranik.koreader.ui.adapters

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import java.net.URLDecoder
import java.util.concurrent.Executors

class BookListAdapter(
        val context: Context,
        private val books: MutableList<BookInfo>,
        private val bookListAdapterClickListener: BookListAdapterClickListener
) :
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
            if (!bookInfo.coverLoaded) {
                bookInfo.coverLoaded = true
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    val contentUriPath = bookInfo.path
                    val uri = Uri.parse(contentUriPath)
                    val doc = DocumentFile.fromSingleUri(context, uri)

                    if (doc != null) {
                        val bookInfoTemp: BookInfo? = EbookHelper.getBookInfoTemporary(context, contentUriPath)

                        if (bookInfoTemp?.cover != null) {
                            books[position].cover = ImageUtils.scaleBitmap(bookInfoTemp.cover!!, 50, 100)
                        } else {
                            books[position].cover =
                                ImageUtils.getBitmap(context, ImageEnum.Ebook)
                        }
                    }
                    handler.post {
                        notifyItemChanged(position)
                    }
                }
            }
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


}