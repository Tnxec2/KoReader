package com.kontranik.koreader.reader

import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.database.BookmarksViewModel
import com.kontranik.koreader.databinding.FragmentBookmarklistBinding
import com.kontranik.koreader.model.Bookmark
import com.kontranik.koreader.utils.BookmarkListAdapter


class BookmarkListFragment : DialogFragment() {

    private lateinit var binding: FragmentBookmarklistBinding

    private var listener: BookmarkListDialogListener? = null
    private lateinit var mBookmarksViewModel: BookmarksViewModel

    private var bookmarkListAdapter: BookmarkListAdapter? = null
    private var bookmarkList: MutableList<Bookmark> = mutableListOf()

    private var longClickedItemIndex: Int? = null

    private var path: String? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface BookmarkListDialogListener {
        fun onSelectBookmark(bookmark: Bookmark)
        fun onAddBookmark()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentBookmarklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener = activity as BookmarkListDialogListener?
        mBookmarksViewModel = ViewModelProvider(this)[BookmarksViewModel::class.java]

        binding.imageButtonBookmarklistBack.setOnClickListener {
            dismiss()
        }
        binding.imageButtonBookmarklistAddBookmark.setOnClickListener {
            addBookmark()
        }

        path = requireArguments().getString(PATH)

        bookmarkListAdapter = BookmarkListAdapter(view.context, R.layout.bookmarklist_item, bookmarkList)
        binding.listViewBookmarklistBookmarks.adapter = bookmarkListAdapter
        registerForContextMenu(binding.listViewBookmarklistBookmarks)

        binding.listViewBookmarklistBookmarks.onItemLongClickListener = OnItemLongClickListener { parent, view, position, id ->
            longClickedItemIndex = position
            false
        }

        val itemListener = OnItemClickListener { parent, v, position, id ->
            open(position)
        }
        binding.listViewBookmarklistBookmarks.onItemClickListener = itemListener

        mBookmarksViewModel.mAllBookmarks.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                bookmarkList.clear()
                bookmarkList.addAll(it.toMutableList ())
                if (bookmarkList.isEmpty()) {
                    binding.textViewBookmarklistStatus.text = getString(R.string.no_bookmarks)
                    binding.textViewBookmarklistStatus.visibility = View.VISIBLE
                } else {
                    binding.textViewBookmarklistStatus.visibility = View.INVISIBLE
                }
            }
            bookmarkListAdapter!!.notifyDataSetChanged()
        })

        if (path != null) mBookmarksViewModel.loadBookmarks(path!!)
    }

    private fun addBookmark() {
        listener!!.onAddBookmark()
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
        mBookmarksViewModel.delete(bookmarkList[position])
        bookmarkList.removeAt(position)
        bookmarkListAdapter?.notifyDataSetChanged()
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


