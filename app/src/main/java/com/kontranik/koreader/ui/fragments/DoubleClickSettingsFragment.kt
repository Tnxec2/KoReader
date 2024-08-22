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
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel
import com.kontranik.koreader.compose.ui.settings.TapZonesDoubleClickSettingsScreen

class DoubleClickSettingsFragment: Fragment()  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val settingsViewModel = ViewModelProvider(requireActivity(), AppViewModelProvider.Factory)[SettingsViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                TapZonesDoubleClickSettingsScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = { requireActivity().supportFragmentManager.popBackStack() },
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}