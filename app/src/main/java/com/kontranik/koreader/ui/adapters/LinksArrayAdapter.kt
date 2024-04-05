package com.kontranik.koreader.ui.adapters

import android.R.layout.simple_list_item_1
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kontranik.koreader.opds.model.Link


class LinksArrayAdapter(context: Context,
                        links: List<Link>) :
    ArrayAdapter<Link>(context, simple_list_item_1, links) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = super.getView(position, convertView, parent)

        val link  = getItem(position)

        if (link != null) {
            (view as TextView).text = link.title
        }

        return super.getView(position, convertView, parent)
    }
}