package com.kontranik.koreader.utils


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord

class FontPickerListItemAdapter(
        context: Context?,
        val textSize: Float,
        private val fonts: MutableList<TypefaceRecord>?,
        private val fontPickerListAdapterClickListener: FontPickerListAdapterClickListener
) :
        RecyclerView.Adapter<FontPickerListItemAdapter.ViewHolder>() {

    private val inflater: LayoutInflater

    interface FontPickerListAdapterClickListener {
        fun onFontlistItemClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.fontpickerlist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val font = fonts!![position]

        holder.fontview.typeface = font.getTypeface()
        holder.fontview.text = font.name
        holder.fontview.textSize = textSize

        holder.itemView.setOnClickListener {
            fontPickerListAdapterClickListener.onFontlistItemClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return fonts!!.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val fontview: TextView = view.findViewById(R.id.textView_fontlist_text)
    }

    init {
        inflater = LayoutInflater.from(context)
    }
}