package com.kontranik.koreader.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kontranik.koreader.R
import com.kontranik.koreader.database.model.Bookmark

class BookmarkListAdapter(
        context: Context?,
        private val layout: Int,
        private val boomarks: List<Bookmark>) : ArrayAdapter<Bookmark?>(context!!, layout, boomarks) {

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return this.boomarks.size
    }

    override fun getItem(position: Int): Bookmark {
        return this.boomarks[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.bookmarklist_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            // 5
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val bookmark: Bookmark = getItem(position)

        holder.textView.text = "${bookmark.text}…"
        holder.positionView.text = "${bookmark.sort}"

        return view
    }

    private class ViewHolder(view: View) {
        val textView = view.findViewById<View>(R.id.textView_bookmarklist_text) as TextView
        val positionView = view.findViewById<View>(R.id.textView_bookmarklist_position) as TextView
    }


}