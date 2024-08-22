package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoScreen
import com.kontranik.koreader.compose.ui.settings.ColorThemeSettingsScreen
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel
import com.kontranik.koreader.database.model.Author


class ColorThemeEditSettingsFragment: Fragment()  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val themeIndex = requireArguments().getInt(THEME_INDEX)

        val settingsViewModel = ViewModelProvider(requireActivity(), AppViewModelProvider.Factory)[SettingsViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                ColorThemeSettingsScreen(
                    themeIndex = themeIndex,
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = { requireActivity().supportFragmentManager.popBackStack()},
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }

    companion object {
        private const val THEME_INDEX = "theme_index"

        fun newInstance(index: Int): ColorThemeEditSettingsFragment {
            val frag = ColorThemeEditSettingsFragment()
            val args = Bundle()
            args.putInt(THEME_INDEX, index)
            frag.arguments = args
            return frag
        }
    }

}