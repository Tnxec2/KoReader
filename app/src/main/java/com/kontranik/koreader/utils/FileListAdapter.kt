package com.kontranik.koreader.utils

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.ImageUtils.getBitmap

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

        if ( fileItem.bookInfo == null ) {
            if ( fileItem.name.endsWith(".epub", true)) {
                val bookInfo = EpubHelper(context, fileItem.path).getBookInfo(fileItem.path)
                fileItem.bookInfo = bookInfo
                if ( bookInfo != null) {
                    holder.nameView.text = bookInfo.title ?: fileItem.name
                    holder.descView.text = bookInfo.authorsAsString()
                    if (bookInfo.cover != null) {
                        bookInfo.cover = ImageUtils.scaleBitmap(bookInfo.cover!!, 50, 100 )
                    } else {
                        bookInfo.cover = getBitmap(context, ImageEnum.Epub)
                    }
                    holder.imageView.setImageBitmap(bookInfo.cover!!)
                } else {
                    holder.nameView.text = fileItem.name
                    holder.descView.text = ""
                    holder.imageView.setImageBitmap(getBitmap(context, fileItem.image))
                }
            }  else {
                holder.nameView.text = fileItem.name
                holder.descView.text = ""
                holder.imageView.setImageBitmap(getBitmap(context, fileItem.image))
            }
        } else {
            holder.nameView.text = fileItem.bookInfo!!.title
            holder.descView.text = fileItem.bookInfo!!.authorsAsString()
            holder.imageView.setImageBitmap(fileItem.bookInfo!!.cover)
        }
        holder.pathView.text = fileItem.path

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
}