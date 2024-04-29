package com.kontranik.koreader.ui.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.ImageUtils.getBitmap
import java.util.concurrent.Executors


class FileListAdapter(
    val context: Context,
    private val fileItems: MutableList<FileItem>,
    private val fileListAdapterClickListener: FileListAdapterClickListener
) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    interface FileListAdapterClickListener {
        fun onFilelistItemDelete(position: Int, item: FileItem)
        fun onFilelistItemClick(position: Int)
        fun onFilelistItemUpdateLibrary(position: Int, fileItem: FileItem)
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
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    val result = fileItems[position]
                    val contentUriPath = result.uriString
                    if ( contentUriPath != null) {
                        val mContext = context

                        result.bookInfo = EbookHelper.getBookInfo(mContext, contentUriPath, result)

                        if ( result.bookInfo != null) {
                            if (result.bookInfo!!.cover != null) {
                                result.bookInfo!!.cover =
                                    ImageUtils.scaleBitmap(result.bookInfo!!.cover!!, 50, 100)
                            } else {
                                result.bookInfo!!.cover = getBitmap(context, ImageEnum.Ebook)
                            }
                        }
                    }
                    handler.post {
                        notifyItemChanged(position)
                    }
                }
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
                popup.inflate(R.menu.menu_file_item_storage_clicked)
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
        } else if (fileItem.isDir) {
            holder.itemView.setOnLongClickListener {
                val popup = PopupMenu(context, holder.itemView)
                popup.inflate(R.menu.menu_file_item_folder_clicked)
                popup.setOnMenuItemClickListener { menuItem: MenuItem? ->
                    if (menuItem != null) {
                        when (menuItem.itemId) {
                            R.id.itemUpdateLibrary -> {
                                fileListAdapterClickListener.onFilelistItemUpdateLibrary(position, fileItem)
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

    override fun onViewRecycled(holder: ViewHolder) {
        with(holder.itemView) {
            setOnClickListener(null)
            setOnLongClickListener(null)
        }
        super.onViewRecycled(holder)
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
}