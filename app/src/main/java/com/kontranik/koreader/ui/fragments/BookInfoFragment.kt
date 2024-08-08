package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoScreen
import com.kontranik.koreader.database.model.Author


class BookInfoFragment: Fragment()  {

    private var mListener: BookInfoListener? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface BookInfoListener {
        fun onBookInfoFragmentReadBook(bookUri: String)
        fun onBookInfoFragmentDeleteBook(bookUri: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val bookPath = requireArguments().getString(BOOK_PATH, null)
        if ( bookPath == null)
            requireActivity().supportFragmentManager.popBackStack()

        return ComposeView(requireContext()).apply {
            setContent {
                BookInfoScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    bookUri = bookPath,
                    navigateBack = {
                        requireActivity().supportFragmentManager.popBackStack()
                    },
                    navigateToAuthor = { author ->
                        onClickAuthorItem(author)
                                       },
                    onReadBook = { bookPath ->
                        mListener?.onBookInfoFragmentReadBook(bookPath)
                        requireActivity().supportFragmentManager.popBackStack()
                    },
                    onDeleteBook = { bookPath ->
                        mListener?.onBookInfoFragmentDeleteBook(bookPath)
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                )
            }
        }
    }

    private fun onClickAuthorItem(author: Author) {
        val fragment = LibraryByTitleFragment.newInstance(author)
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_library_by_title")
            .addToBackStack("fragment_library_by_title")
            .commit()
    }

    fun setListener(listener: BookInfoListener) {
        this.mListener = listener
    }

    override fun onDetach() {
        super.onDetach()
        this.mListener = null
    }

    companion object {
        private const val BOOK_PATH = "book_path"

        fun newInstance(bookPath: String?): BookInfoFragment {
            val frag = BookInfoFragment()
            val args = Bundle()
            args.putString(BOOK_PATH, bookPath)
            frag.arguments = args
            return frag
        }
    }

}