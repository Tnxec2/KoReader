package com.kontranik.koreader.ui.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.OpdsTypes
import com.kontranik.koreader.ui.fragments.OpdsEntryListFragment
import com.kontranik.koreader.utils.ImageUtils
import java.util.concurrent.Executors


class OpdsEntryListAdapter(
    val context: Context,
    private val entrys: MutableList<Entry>,
    private val listAdapterClickListener: OpdsEntryListAdapterClickListener
) :
    RecyclerView.Adapter<OpdsEntryListAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private var startUrl: String? = null

    interface OpdsEntryListAdapterClickListener {
        fun onOpdsEntrylistItemClick(entry: Entry)
        fun onOpdslistItemDelete(position: Int)
        fun onOpdslistItemEdit(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.opdsentrylist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry: Entry = entrys[position]

        holder.titleView.text = entry.title

        if (entry.author != null) {
            holder.authorView.visibility = View.VISIBLE
            holder.authorView.text = entry.author.toString()
        } else {
            if (entry.content?.type == OpdsTypes.TYPE_TEXT) {
                holder.authorView.visibility = View.VISIBLE
                holder.authorView.text = entry.content.data
            } else {
                holder.authorView.visibility = View.GONE
            }
        }

        if (entry.thumbnailBitmap != null) {
            holder.iconView.setImageBitmap(entry.thumbnailBitmap)
            holder.iconView.visibility = View.VISIBLE
        } else {
            holder.iconView.visibility = View.GONE
            if (!entry.thumbnailBitmapLoaded) {
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    entrys[position].thumbnailBitmapLoaded = true
                    entrys[position].thumbnail?.href?.let {
                        entrys[position].thumbnailBitmap = ImageUtils.drawableFromUrl(
                            it, startUrl
                        )
                    }
                    handler.post {
                        notifyItemChanged(position)
                    }
                }
            }
        }

        holder.contentView.setOnClickListener {
            listAdapterClickListener.onOpdsEntrylistItemClick(entry)
        }

        if (startUrl == OpdsEntryListFragment.OVERVIEW) {
            holder.menuView.visibility = View.VISIBLE
            holder.menuView.setOnClickListener {
                val popup = PopupMenu(context, holder.menuView)
                popup.inflate(R.menu.menu_opds_item_clicked)
                popup.setOnMenuItemClickListener { item: MenuItem? ->
                    if (item != null) {
                        when (item.itemId) {
                            R.id.itemDelete ->
                                listAdapterClickListener.onOpdslistItemDelete(position)

                            R.id.itemEdit ->
                                listAdapterClickListener.onOpdslistItemEdit(position)

                        }
                    }
                    false
                }
                popup.show()
            }
        } else {
            holder.menuView.visibility = View.GONE
        }

        holder.pathView.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return entrys.size
    }

    fun setStartUrl(url: String?) {
        startUrl = url
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val contentView = itemView.findViewById<View>(R.id.opdsentry_listitem_container) as LinearLayout
        val titleView = itemView.findViewById<View>(R.id.opdsentry_listitem_title) as TextView
        val authorView = itemView.findViewById<View>(R.id.opdsentry_listitem_author) as TextView
        val pathView = itemView.findViewById<View>(R.id.opdsentry_listitem_content) as TextView
        val iconView = itemView.findViewById<View>(R.id.opdsentry_listitem_cover) as ImageView
        val menuView = itemView.findViewById<View>(R.id.opdsentry_listitem_menu) as ImageView
    }
}