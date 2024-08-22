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
import com.kontranik.koreader.compose.ui.settings.RootSettingsScreen
import com.kontranik.koreader.database.model.Author


class SettingsComposeFragment: Fragment()  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val mReaderActivityViewModel = ViewModelProvider(requireActivity(), AppViewModelProvider.Factory)[ReaderActivityViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                RootSettingsScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = {
                        mReaderActivityViewModel.loadSettings(requireActivity())
                        requireActivity().supportFragmentManager.popBackStack()
                    },
                    navigateToInterfaceSettings = {
                        openInterfaceSettings()
                    },
                    navigateToColorThemeSettings = {
                        openColorThemeSettings()
                    },
                    navigateToTextSettings = {
                        openTextSettingsSettings()
                    },
                    navigateToTapZonesSettings = {
                        openTapZonesSettings()
                    })
            }
        }
    }

    private fun openInterfaceSettings() {
        val fragment = InterfaceSettingsFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_interface_settings")
            .addToBackStack("fragment_interface_settings")
            .commit()
    }

    private fun openColorThemeSettings() {
        val fragment = ColorThemeSettingsFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_color_theme_settings")
            .addToBackStack("fragment_color_theme_settings")
            .commit()
    }

    private fun openTextSettingsSettings() {
        val fragment = TextSettingsFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_text_settings")
            .addToBackStack("fragment_text_settings")
            .commit()
    }
    private fun openTapZonesSettings() {
        val fragment = TapZonesSettingsFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_tapzones_settings")
            .addToBackStack("fragment_tapzones_settings")
            .commit()
    }
}