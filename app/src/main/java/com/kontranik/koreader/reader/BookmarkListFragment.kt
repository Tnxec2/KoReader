package com.kontranik.koreader.reader

import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.database.BookmarkService
import com.kontranik.koreader.database.BookmarksDatabaseAdapter
import com.kontranik.koreader.model.Bookmark
import com.kontranik.koreader.utils.BookmarkListAdapter


class BookmarkListFragment : DialogFragment() {

    private var listener: BookmarkListDialogListener? = null
    private var service: BookmarkService? = null

    private var listView: ListView? = null
    private var bookmarkListAdapter: BookmarkListAdapter? = null
    private var bookmarkList: MutableList<Bookmark> = mutableListOf()

    private var longClickedItemIndex: Int? = null

    var path: String? = null
    private var statusText: TextView? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface BookmarkListDialogListener {
        fun onSelectBookmark(bookmark: Bookmark)
        fun onAddBookmark(): Boolean
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_bookmarklist, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener = activity as BookmarkListDialogListener?
        service = BookmarkService(BookmarksDatabaseAdapter(view.context))

        val close = view.findViewById<ImageButton>(R.id.imageButton_bookmarklist_back)
        close.setOnClickListener {
            dismiss()
        }
        val add = view.findViewById<ImageButton>(R.id.imageButton_bookmarklist_addBookmark)
        add.setOnClickListener {
            addBookmark()
        }

        statusText = view.findViewById(R.id.textView_bookmarklist_status)
        path = requireArguments().getString(PATH)

        loadBookmarks(path!!)

        listView = view.findViewById(R.id.listView_bookmarklist_bookmarks)

        bookmarkListAdapter = BookmarkListAdapter(view.context, R.layout.bookmarklist_item, bookmarkList)
        listView!!.adapter = bookmarkListAdapter
        registerForContextMenu(listView!!)

        listView!!.onItemLongClickListener = OnItemLongClickListener { parent, view, position, id ->
            longClickedItemIndex = position
            false
        }

        val itemListener = OnItemClickListener { parent, v, position, id ->
            open(position)
        }
        listView!!.onItemClickListener = itemListener

    }

    private fun addBookmark() {
        val result = listener!!.onAddBookmark()
        if ( result ) {
            refreshBookmarks()
        }
    }

    private fun refreshBookmarks() {
        loadBookmarks(path!!)
        // fire the event
        bookmarkListAdapter!!.notifyDataSetChanged()
    }

    private fun loadBookmarks(path: String) {
        bookmarkList.clear()
        bookmarkList.addAll(service!!.getByPath(path).toMutableList())
        if ( bookmarkList.isEmpty() ) {
            statusText!!.text = getString(R.string.no_bookmarks)
            statusText!!.visibility = View.VISIBLE
        } else {
            statusText!!.visibility = View.INVISIBLE
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        menu.add(0, MENU_ITEM_ID_OPEN, 0, getString(R.string.open))
        menu.add(0, MENU_ITEM_ID_DELETE, 0, getString(R.string.delete))

        menu.getItem(0).setOnMenuItemClickListener {
            open(longClickedItemIndex!!)
            false
        }
        menu.getItem(1).setOnMenuItemClickListener {
            delete(longClickedItemIndex!!)
            false
        }
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    private fun delete(position: Int) {
        service!!.deleteBookmark(bookmarkList[position])
        refreshBookmarks()
    }

    private fun open(position: Int) {
        val selectedItem = bookmarkList[position]
        listener!!.onSelectBookmark(selectedItem)
        dismiss()
    }

    companion object {
        private const val MENU_ITEM_ID_OPEN = 0
        private const val MENU_ITEM_ID_DELETE = 1
        const val PATH = "path"
        fun newInstance(path: String): BookmarkListFragment {
            val frag = BookmarkListFragment()
            val args = Bundle()
            args.putString(PATH, path)
            frag.arguments = args
            return frag
        }
    }

}


