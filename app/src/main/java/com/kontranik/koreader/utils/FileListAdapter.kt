package com.kontranik.koreader.utils

import android.content.Context
import android.os.AsyncTask
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
import com.kontranik.koreader.utils.ImageUtils.getBitmap
import java.lang.ref.WeakReference

class FileListAdapter(
        val context: Context,
        private val fileitems: List<FileItem>,
        private val fileListAdapterClickListener: FileListAdapterClickListener) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    interface FileListAdapterClickListener {
        fun onFilelistItemClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.filelist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileItem: FileItem = fileitems[position]

        holder.nameView.text = fileItem.name
        holder.descView.text = ""
        holder.imageView.setImageBitmap(getBitmap(context, fileItem.image))
        holder.pathView.text = fileItem.path
        if ( ! fileItem.isDir ) {
            if ( fileItem.bookInfo == null ) {
                val asyncTask = ReadBookInfoAsync(context, holder)
                asyncTask.execute(fileItem)
            } else {
                holder.nameView.text = fileItem.bookInfo!!.title
                holder.descView.text = fileItem.bookInfo!!.authorsAsString()
                holder.pathView.text = fileItem.name
                if (fileItem.bookInfo!!.cover == null) {
                    fileItem.bookInfo!!.cover = getBitmap(context, ImageEnum.Ebook)
                }
                holder.imageView.setImageBitmap(fileItem.bookInfo!!.cover!!)
            }
        }

        holder.itemView.setOnClickListener {
            fileListAdapterClickListener.onFilelistItemClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return fileitems.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<View>(R.id.fileimage) as ImageView
        val nameView = itemView.findViewById<View>(R.id.filename) as TextView
        val descView = itemView.findViewById<View>(R.id.filedesc) as TextView
        val pathView = itemView.findViewById<View>(R.id.filepath) as TextView
    }

    class ReadBookInfoAsync(context: Context, val holder: ViewHolder) : AsyncTask<FileItem?, Int?, FileItem?>() {
        // Weak references will still allow the Activity to be garbage-collected
        private val weakContext: WeakReference<Context> = WeakReference(context)

        override fun doInBackground(vararg params: FileItem?): FileItem? {
            var result: FileItem? = null
            if (params.isNotEmpty()) {
                result = params.first()
                val contentUriPath = result!!.uriString
                if ( contentUriPath != null) {
                    val mContext = weakContext.get()
                    if (contentUriPath.endsWith(".epub", ignoreCase = true)) {
                        result.bookInfo = EpubHelper(mContext!!, contentUriPath).getBookInfoTemporary(contentUriPath)
                    } else if (contentUriPath.endsWith(".fb2", ignoreCase = true)
                            || contentUriPath.endsWith(".fb2.zip", ignoreCase = true)) {
                        result.bookInfo = FB2Helper(mContext!!, contentUriPath).getBookInfoTemporary(contentUriPath)
                    } else {
                        result.bookInfo = BookInfo(result)
                    }
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

        override fun onPostExecute(fileItem: FileItem?) {
            if (fileItem?.bookInfo != null) {
                holder.nameView.text = fileItem.bookInfo!!.title
                holder.descView.text = fileItem.bookInfo!!.authorsAsString()
                holder.pathView.text = fileItem.name
                if (fileItem.bookInfo!!.cover != null) {
                    fileItem.bookInfo!!.cover = ImageUtils.scaleBitmap(fileItem.bookInfo!!.cover!!, 50, 100)
                } else {
                    fileItem.bookInfo!!.cover = getBitmap(weakContext.get()!!, ImageEnum.Ebook)
                }
                holder.imageView.setImageBitmap(fileItem.bookInfo!!.cover!!)
            }
        }

    }
}