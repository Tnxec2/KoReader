package com.kontranik.koreader.ui.adapters

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.parser.epubreader.EpubHelper
import com.kontranik.koreader.parser.fb2reader.FB2Helper
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.ImageUtils.getBitmap


class FileListAdapter(
    val context: Context,
    private val fileItems: MutableList<FileItem>,
    private val fileListAdapterClickListener: FileListAdapterClickListener
) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    interface FileListAdapterClickListener {
        fun onFilelistItemDelete(position: Int, item: FileItem)
        fun onFilelistItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.filelist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileItem: FileItem = fileItems[position]

        holder.nameView.text = fileItem.name
        holder.descView.text = ""
        holder.imageView.setImageBitmap(getBitmap(context, fileItem.image))
        holder.pathView.text = fileItem.path
        if (!fileItem.isDir) {
            if (fileItem.bookInfo == null) {
                val asyncTask = ReadBookInfoAsync(this)
                asyncTask.execute(position)
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
            fileListAdapterClickListener.onFilelistItemClick(position)
        }

        if ( fileItem.isStorage ) {
            holder.itemView.setOnLongClickListener {
                val popup = PopupMenu(context, holder.nameView)
                popup.inflate(R.menu.menu_file_item_clicked)
                popup.setOnMenuItemClickListener { item: MenuItem? ->
                    if (item != null) {
                        when (item.itemId) {
                            R.id.itemDelete -> {
                                fileListAdapterClickListener.onFilelistItemDelete(position, fileItem)
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

        override fun getItemCount(): Int {
        return fileItems.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<View>(R.id.fileimage) as ImageView
        val nameView = itemView.findViewById<View>(R.id.filename) as TextView
        val descView = itemView.findViewById<View>(R.id.filedesc) as TextView
        val pathView = itemView.findViewById<View>(R.id.filepath) as TextView
    }


    class ReadBookInfoAsync(private val adapter: FileListAdapter) : AsyncTask<Int?, Int?, FileItem?>() {

        var position = 0
        override fun doInBackground(vararg params: Int?): FileItem? {
            var result: FileItem? = null
            if (params.isNotEmpty() && params.first() != null) {
                position = params.first()!!
                if ( position >= adapter.fileItems.size ) return null
                result = adapter.fileItems[position]
                val contentUriPath = result.uriString
                if ( contentUriPath != null) {
                    val mContext = adapter.context

                    if (contentUriPath.endsWith(".epub", ignoreCase = true)) {
                        result.bookInfo = EpubHelper(mContext, contentUriPath).getBookInfoTemporary(contentUriPath)
                    } else if (contentUriPath.endsWith(".fb2", ignoreCase = true)
                            || contentUriPath.endsWith(".fb2.zip", ignoreCase = true)) {
                        result.bookInfo = FB2Helper(mContext, contentUriPath).getBookInfoTemporary(contentUriPath)
                    } else {
                        result.bookInfo = BookInfo(result)
                    }

                    if ( result.bookInfo != null) {
                        if (result.bookInfo!!.cover != null) {
                            result.bookInfo!!.cover =
                                ImageUtils.scaleBitmap(result.bookInfo!!.cover!!, 50, 100)
                        } else {
                            result.bookInfo!!.cover = getBitmap(adapter.context, ImageEnum.Ebook)
                        }
                    }
                }
            }

            return result
        }

        override fun onPostExecute(result: FileItem?) {
            if ( result != null) adapter.notifyItemChanged(position)
        }
    }
}