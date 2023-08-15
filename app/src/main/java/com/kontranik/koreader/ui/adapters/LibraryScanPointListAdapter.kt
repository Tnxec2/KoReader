package com.kontranik.koreader.ui.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R

class LibraryScanPointListAdapter(
        val context: Context,
        private val scanPoints: List<String>,
        private val mListener: LibraryScanPointListAdapterClickListener
        ) : RecyclerView.Adapter<LibraryScanPointListAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    interface LibraryScanPointListAdapterClickListener {
        fun onLibraryScanPointListItemDelete(position: Int, item: String)
        fun onLibraryScanPointListItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.library_scanlist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val scanPointItem: String = scanPoints[position]

        val directoryUri = Uri.parse(scanPointItem)
            ?: throw IllegalArgumentException("Must pass URI of directory to open")
        val documentsTree = DocumentFile.fromTreeUri(
            context, directoryUri)
        holder.textView.text =  documentsTree?.uri?.pathSegments?.last() ?: scanPointItem

        holder.ll.setOnClickListener {
            mListener.onLibraryScanPointListItemClick(position)
        }

        holder.ll.setOnLongClickListener {
            onDeleteItem(holder, position, scanPointItem)
        }

    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.ll.setOnLongClickListener(null)
        holder.ll.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    private fun onDeleteItem(holder: ViewHolder, position: Int, scanPointItem: String): Boolean {
        val popup = PopupMenu(context, holder.textView)
        popup.inflate(R.menu.menu_file_item_storage_clicked)
        popup.setOnMenuItemClickListener { item: MenuItem? ->
            if (item != null) {
                when (item.itemId) {
                    R.id.itemDelete -> {
                        mListener.onLibraryScanPointListItemDelete(position, scanPointItem)
                    }
                }
            }
            false
        }
        popup.show()
        return true
    }

    override fun getItemCount(): Int {
        return scanPoints.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<View>(R.id.textView_libraryscan_itemname) as TextView
        val ll = view.findViewById<View>(R.id.ll_library_scanlist_item) as LinearLayout
    }


}