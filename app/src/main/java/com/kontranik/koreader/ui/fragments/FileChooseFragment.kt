package com.kontranik.koreader.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

                    onAddToStorage = {
                        performFileSearchToAddStorage()
                    },
                    readerActivityViewModel = mReaderActivityViewModel,
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

    /**
     * Fires an intent to spin up the "file chooser" UI and select a directory.
     */
    private fun performFileSearchToAddStorage() {
        Toast.makeText(
            requireContext(),
            "Select directory or storage from dialog, and grant access",
            Toast.LENGTH_LONG)
            .show()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION   // write permission to remove book
        startForResultPickFileToStorage.launch(Intent.createChooser(intent, "Select file storage"))
    }

    private val startForResultPickFileToStorage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the Intent
            result.data?.data?.let {
                requireActivity().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                mFileChooseFragmentViewModel.addStoragePath(it.toString())
            }
        }
    }

}