package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.compose.ui.mainmenu.MainMenuScreen
import com.kontranik.koreader.utils.PrefsHelper

class MainMenuFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        var bookPath: String? = null
        val prefs = requireActivity().getSharedPreferences(ReaderActivity.PREFS_FILE, AppCompatActivity.MODE_PRIVATE)

        if ( prefs.contains(PrefsHelper.PREF_BOOK_PATH) ) {
            bookPath = prefs.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        return ComposeView(requireContext()).apply {
            setContent {
                MainMenuScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = { requireActivity().supportFragmentManager.popBackStack() },
                    navigateToOpenFile = { openFile() },
                    navigateToLastOpened = { openLastOpened() },
                    navigateToLibrary = { openLibrary() },
                    navigateToOpdsNetworkLibrary = { openOpds() },
                    navigateToSettings = { settings() },
                    bookPath = remember { mutableStateOf(bookPath) },
                    navigateToBookInfo = { openBookInfo(it) }
                )
            }
        }
    }

    private fun openFile() {
        val fragment = FileChooseFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_open_file")
            .addToBackStack("fragment_open_file")
            .commit()
    }

    private fun settings() {
        val fragment = SettingsFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_settings")
            .addToBackStack("fragment_settings")
            .commit()
    }

    private fun openLastOpened() {
        val fragment = LastOpenedBookListFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_last_opened")
            .addToBackStack("fragment_last_opened")
            .commit()
    }

    private fun openLibrary() {
        val fragment = LibraryMainMenuFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_library")
            .addToBackStack("fragment_library")
            .commit()
    }

    private fun openOpds() {
        val fragment = OpdsEntryListFragment.newInstance()

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_opds")
            .addToBackStack("fragment_opds")
            .commit()
    }

    private fun openBookInfo(bookUri: String?) {
        if ( bookUri != null) {
            val bookInfoFragment: BookInfoFragment = BookInfoFragment.newInstance(bookUri)
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, bookInfoFragment, "fragment_bookinfo_from_mainmenu")
                .addToBackStack("fragment_bookinfo_from_mainmenu")
                .commit()
        }
    }

}