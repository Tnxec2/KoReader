package com.kontranik.koreader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.compose.ui.bookmarks.BoomkmarksScreen
import com.kontranik.koreader.database.BookmarksViewModel
import com.kontranik.koreader.database.model.Bookmark


class BookmarkListFragment : Fragment() {

    private var mListener: BookmarkListDialogListener? = null
    private lateinit var mBookmarksViewModel: BookmarksViewModel

    private var path: String? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface BookmarkListDialogListener {
        fun onSelectBookmarkBookmarkListFragment(bookmark: Bookmark)
        fun onAddBookmarkBookmarkListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        mBookmarksViewModel = ViewModelProvider(this,
            AppViewModelProvider.Factory)[BookmarksViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                BoomkmarksScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = {
                        requireActivity().supportFragmentManager.popBackStack()
                    },
                    navigateToBookmark = { bookmark ->
                        mListener?.onSelectBookmarkBookmarkListFragment(bookmark)
                        closeBackStack()
                    },
                    addBookmark = {
                        mListener?.onAddBookmarkBookmarkListFragment()
                        closeBackStack()
                    }
                )
            }
        }
    }

    private fun closeBackStack() {
        val fm = requireActivity().supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
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

    companion object {
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


