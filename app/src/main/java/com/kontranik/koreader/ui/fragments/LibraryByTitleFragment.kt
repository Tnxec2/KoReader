package com.kontranik.koreader.ui.fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.compose.ui.library.LibraryViewModel
import com.kontranik.koreader.compose.ui.library.bytitle.LibraryByTitleScreen
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.LibraryItemWithAuthors


open class LibraryByTitleFragment : Fragment(),
    BookInfoFragment.BookInfoListener {

    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel
    private lateinit var mFileChooseFragmentViewModel: FileChooseFragmentViewModel
    private lateinit var libraryViewModel: LibraryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        mReaderActivityViewModel = ViewModelProvider(requireActivity())[ReaderActivityViewModel::class.java]
        mFileChooseFragmentViewModel = ViewModelProvider(this, AppViewModelProvider.Factory)[FileChooseFragmentViewModel::class.java]
        libraryViewModel = ViewModelProvider(this, AppViewModelProvider.Factory)[LibraryViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                LibraryByTitleScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = {
                        requireActivity().supportFragmentManager.popBackStack()
                    },
                    navigateToBook = {
                        onClickLibraryItem(it)
                    }
                )
            }
        }
    }

    private fun onClickLibraryItem( libraryItem: LibraryItemWithAuthors) {
        val bookPathUri = libraryItem.libraryItem.path
        try {
            val inputStream = requireContext().contentResolver.openInputStream(Uri.parse(bookPathUri))
            inputStream?.close()

            val bookInfoFragment = BookInfoFragment.newInstance(bookPathUri)
            bookInfoFragment.setListener(this)
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, bookInfoFragment, "fragment_bookinfo_from_library_by_title")
                .addToBackStack("fragment_bookinfo_from_library_by_title")
                .commit()
        } catch (e: Exception) {
            AlertDialog.Builder(context)
                .setTitle("Book does not exist")
                .setMessage("Are you sure you want to delete this book from library?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    android.R.string.yes
                ) { _, _ ->
                    libraryViewModel.delete(libraryItem)
                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    override fun onBookInfoFragmentReadBook(bookUri: String) {
        mFileChooseFragmentViewModel.savePrefsOpenedBook(bookUri)
        mReaderActivityViewModel.setBookPath(requireContext(), bookUri)

        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onBookInfoFragmentDeleteBook(bookUri: String) {
        // TODO("Not yet implemented")
    }

    companion object {
        const val KEY_AUTHOR = "AUTHOR"

        fun newInstance(
            author: Author?
        ): LibraryByTitleFragment {
            val frag = LibraryByTitleFragment()
            val args = Bundle()
            author?.let{ args.putSerializable(KEY_AUTHOR, it) }
            frag.arguments = args

            return frag
        }
    }
}