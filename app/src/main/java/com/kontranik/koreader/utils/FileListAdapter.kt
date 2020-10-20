package com.kontranik.koreader.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.ImageUtils.getBitmap

class FileListAdapter(context: Context?, private val layout: Int, private val fileitems: List<FileItem>) : ArrayAdapter<FileItem?>(context!!, layout, fileitems) {
    private val inflater: LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(layout, parent, false)
        val imageView = view.findViewById<View>(R.id.fileimage) as ImageView
        val nameView = view.findViewById<View>(R.id.filename) as TextView
        val pathView = view.findViewById<View>(R.id.filepath) as TextView
        val fileItem = fileitems[position]
        if (fileItem.image != null) {
            val bmp = getBitmap(context, fileItem.image, fileItem.path)
            if (bmp != null) {
                imageView.setImageBitmap(bmp)
            }
        }
        nameView.text = fileItem.name
        pathView.text = fileItem.path
        return view
    }

    init {
        inflater = LayoutInflater.from(context)
    }
}