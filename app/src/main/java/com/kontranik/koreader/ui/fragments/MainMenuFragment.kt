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
import com.kontranik.koreader.compose.ui.mainmenu.MainMenuScreen
import com.kontranik.koreader.utils.PrefsHelper

class MainMenuFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return ComposeView(requireContext()).apply {
            setContent {
                MainMenuScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = { requireActivity().supportFragmentManager.popBackStack() },
                    navigateToOpenFile = { openFile() },
                    navigateToLastOpened = { openLastOpened() },
                    navigateToBookmarks = { openBookmarks() },
                    navigateToLibrary = { openLibrary() },
                    navigateToOpdsNetworkLibrary = { openOpds() },
                    navigateToSettings = { settings() },
                    navigateToBookInfo = { openBookInfo(it) }
                )
            }
        }
    }

    private fun openBookmarks() {
        if (PrefsHelper.bookPath == null) return
        val fragment = BookmarkListFragment.newInstance(PrefsHelper.bookPath!!)

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_bookmark_list")
            .addToBackStack("fragment_bookmark_list")
            .commit()
    }

    private fun openFile() {
        val fragment = FileChooseFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_open_file")
            .addToBackStack("fragment_open_file")
            .commit()
    }

    private fun settings() {
        val fragment = SettingsComposeFragment()

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