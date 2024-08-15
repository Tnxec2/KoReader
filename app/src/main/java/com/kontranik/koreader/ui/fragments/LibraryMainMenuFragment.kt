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
import com.kontranik.koreader.compose.ui.library.main.LibraryMainMenuScreen

class LibraryMainMenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LibraryMainMenuScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = { requireActivity().supportFragmentManager.popBackStack() },
                    navigateToBooksByTitle = { openByTitle() },
                    navigateToBooksByAuthor = { openByAuthor() },
                    navigateToSettings = { settings() })
            }
        }
    }

    private fun openByTitle() {
        val fragment = LibraryByTitleFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container_view, fragment, "fragment_library_by_title")
            .addToBackStack("fragment_library_by_title")
            .commit()
    }

    private fun openByAuthor() {
        val fragment = LibraryByAuthorFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container_view, fragment, "fragment_library_by_author")
            .addToBackStack("fragment_library_by_author")
            .commit()
    }

    private fun settings() {
        val fragment = LibrarySettingsFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container_view, fragment, "fragment_library_settings")
            .addToBackStack("fragment_library_settings")
            .commit()
    }


}