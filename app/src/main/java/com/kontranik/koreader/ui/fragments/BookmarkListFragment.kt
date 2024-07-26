package com.kontranik.koreader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import com.kontranik.koreader.database.BookmarksViewModel
import com.kontranik.koreader.database.BookmarksViewModelFactory
import com.kontranik.koreader.databinding.FragmentBookmarklistBinding
import com.kontranik.koreader.database.model.Bookmark
import com.kontranik.koreader.ui.adapters.BookmarkListAdapter


class BookmarkListFragment : DialogFragment() {

    private lateinit var binding: FragmentBookmarklistBinding

    private var mListener: BookmarkListDialogListener? = null
    private lateinit var mBookmarksViewModel: BookmarksViewModel

    private var bookmarkListAdapter: BookmarkListAdapter? = null
    private var bookmarkList: MutableList<Bookmark> = mutableListOf()

    private var longClickedItemIndex: Int? = null

    private var path: String? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface BookmarkListDialogListener {
        fun onSelectBookmarkBookmarkListFragment(bookmark: Bookmark)
        fun onAddBookmarkBookmarkListFragment()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBookmarksViewModel = ViewModelProvider(this,
            BookmarksViewModelFactory((requireContext().applicationContext as KoReaderApplication)
            .bookmarksRepository))[BookmarksViewModel::class.java]

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

        binding.listViewBookmarklistBookmarks.onItemLongClickListener = OnItemLongClickListener { _, _, position, _ ->
            longClickedItemIndex = position
            false
        }

        val itemListener = OnItemClickListener { _, _, position, _ ->
            open(position)
        }
        binding.listViewBookmarklistBookmarks.onItemClickListener = itemListener

        mBookmarksViewModel.mAllBookmarks.observe(viewLifecycleOwner) {
            if (it != null) {
                bookmarkList.clear()
                bookmarkList.addAll(it.toMutableList())
                if (bookmarkList.isEmpty()) {
                    binding.textViewBookmarklistStatus.text = getString(R.string.no_bookmarks)
                    binding.textViewBookmarklistStatus.visibility = View.VISIBLE
                } else {
                    binding.textViewBookmarklistStatus.visibility = View.INVISIBLE
                }
            }
            bookmarkListAdapter!!.notifyDataSetChanged()
        }

        if (path != null) mBookmarksViewModel.loadBookmarks(path!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BookmarkListDialogListener) {
            mListener = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement BookmarkListDialogListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun addBookmark() {
        mListener?.onAddBookmarkBookmarkListFragment()
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
        mListener?.onSelectBookmarkBookmarkListFragment(selectedItem)
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


