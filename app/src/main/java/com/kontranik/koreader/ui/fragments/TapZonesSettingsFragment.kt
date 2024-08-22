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
import com.kontranik.koreader.compose.ui.settings.TapZonesSettingsScreen


class TapZonesSettingsFragment: Fragment()  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {


        return ComposeView(requireContext()).apply {
            setContent {
                TapZonesSettingsScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = { requireActivity().supportFragmentManager.popBackStack() },
                    navigateToSettingsTapZonesOneClick = {
                        val fragment = OneClickSettingsFragment()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container_view, fragment, "fragment_tapzones_one_click_settings")
                            .addToBackStack("fragment_tapzones_one_click_settings")
                            .commit()
                    },
                    navigateToSettingsTapZonesDoubleClick = {
                        val fragment = DoubleClickSettingsFragment()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container_view, fragment, "fragment_tapzones_double_click_settings")
                            .addToBackStack("fragment_tapzones_double_click_settings")
                            .commit()
                    },
                    navigateToSettingsTapZonesLongClick = {
                        val fragment = LongClickSettingsFragment()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container_view, fragment, "fragment_tapzones_long_click_settings")
                            .addToBackStack("fragment_tapzones_long_click_settings")
                            .commit()
                    },
                )
            }
        }
    }

}