package com.kontranik.koreader.ui.fragments

import android.content.Context
import android.content.SharedPreferences
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
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.ReaderActivityViewModelFactory
import com.kontranik.koreader.compose.ui.lastopened.LastOpenedScreen
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.BookStatusViewModelFactory

class LastOpenedBookListFragment :
    Fragment(),
    BookInfoFragment.BookInfoListener {

    private lateinit var mBookStatusViewModel: BookStatusViewModel
    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        mBookStatusViewModel = ViewModelProvider(this,
            BookStatusViewModelFactory((requireContext().applicationContext as KoReaderApplication)
                .bookStatusRepository))[BookStatusViewModel::class.java]

        mReaderActivityViewModel = ViewModelProvider(this,
            ReaderActivityViewModelFactory((requireContext().applicationContext as KoReaderApplication)
                .bookStatusRepository))[ReaderActivityViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                LastOpenedScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = { requireActivity().supportFragmentManager.popBackStack()},
                    navigateToBookInfo = { bookPath ->
                        openBookInfo(bookPath)
                    }
                )

            }
        }
    }

    private fun savePrefs(uriString: String?) {
        val settings: SharedPreferences? = requireActivity()
            .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        val prefEditor = settings!!.edit()
        if ( uriString != null) prefEditor!!.putString(PREF_LAST_PATH, uriString)
        prefEditor.apply()
    }



    private fun openBookInfo(bookPathUri: String?) {
        if ( bookPathUri != null) {
            val bookInfoFragment = BookInfoFragment.newInstance(bookPathUri)
            bookInfoFragment.setListener(this)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, bookInfoFragment, "fragment_bookinfo_from_lastopened")
                .addToBackStack("fragment_bookinfo_from_lastopened")
                .commit()
        }
    }

    override fun onBookInfoFragmentReadBook(bookUri: String) {
        savePrefs(bookUri)
        mReaderActivityViewModel.setBookPath(requireContext(), bookUri)

        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onBookInfoFragmentDeleteBook(bookUri: String) {
        if ( mReaderActivityViewModel.deleteBook(bookUri)) {
            mBookStatusViewModel.deleteByPath(bookUri)
        }
    }
}