package com.kontranik.koreader.ui.fragments

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
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoScreen
import com.kontranik.koreader.compose.ui.settings.ColorSettingsScreen
import com.kontranik.koreader.compose.ui.settings.ColorThemeSettingsScreen
import com.kontranik.koreader.compose.ui.settings.InterfaceSettingsScreen
import com.kontranik.koreader.compose.ui.settings.RootSettingsScreen
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel
import com.kontranik.koreader.database.model.Author


class ColorThemeSettingsFragment: Fragment()  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val settingsViewModel = ViewModelProvider(requireActivity(), AppViewModelProvider.Factory)[SettingsViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                ColorSettingsScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = { requireActivity().supportFragmentManager.popBackStack() },
                    navigateToTheme = { openThemeFragment(it) },
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }

    private fun openThemeFragment(index: Int) {
        val fragment = ColorThemeEditSettingsFragment.newInstance(index)
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_color_theme_edit_settings")
            .addToBackStack("fragment_color_theme_edit_settings")
            .commit()
    }
}