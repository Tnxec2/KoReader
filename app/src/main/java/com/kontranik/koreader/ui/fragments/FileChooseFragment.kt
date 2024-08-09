package com.kontranik.koreader.ui.fragments

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
import com.kontranik.koreader.compose.ui.openfile.OpenFileScreen


class FileChooseFragment : Fragment(),
        BookInfoFragment.BookInfoListener {

    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel
    private lateinit var mFileChooseFragmentViewModel: FileChooseFragmentViewModel
    private lateinit var mLibraryViewModel: LibraryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mReaderActivityViewModel = ViewModelProvider(requireActivity(), AppViewModelProvider.Factory)[ReaderActivityViewModel::class.java]

        mFileChooseFragmentViewModel = ViewModelProvider(requireActivity(),
            AppViewModelProvider.Factory)[FileChooseFragmentViewModel::class.java]

        mLibraryViewModel = ViewModelProvider(requireActivity(), AppViewModelProvider.Factory)[LibraryViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                OpenFileScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = { requireActivity().supportFragmentManager.popBackStack() },
                    navigateToBookInfo = { openBookInfo(it) },
                    fileChooseFragmentViewModel = mFileChooseFragmentViewModel,
                    libraryViewModel = mLibraryViewModel,
                )
            }
        }
    }

    private fun openBook(uriString: String) {
        mFileChooseFragmentViewModel.savePrefsOpenedBook(uriString)
        mReaderActivityViewModel.setBookPath(requireContext(), uriString)

        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun deleteBook(uriString: String?) {
        if ( mReaderActivityViewModel.deleteBook(uriString)) {
            mFileChooseFragmentViewModel.removeBookFromList(uriString)
        }
    }

    private fun openBookInfo(bookUri: String?) {
        if ( bookUri != null) {
            val bookInfoFragment = BookInfoFragment.newInstance(bookUri)
            bookInfoFragment.setListener(this)
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, bookInfoFragment, "fragment_bookinfo_from_filechoose")
                .addToBackStack("fragment_bookinfo_from_filechoose")
                .commit()
        }
    }

    override fun onBookInfoFragmentReadBook(bookUri: String) {
        openBook(bookUri)
    }

    override fun onBookInfoFragmentDeleteBook(bookUri: String) {
        deleteBook(bookUri)
    }
}