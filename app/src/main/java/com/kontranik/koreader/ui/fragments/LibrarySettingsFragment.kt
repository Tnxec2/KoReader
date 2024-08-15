package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.kontranik.koreader.compose.ui.library.settings.LibrarySettingsScreen

class LibrarySettingsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return ComposeView(requireContext()).apply {
            setContent {
                LibrarySettingsScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = {
                        requireActivity().supportFragmentManager.popBackStack()
                    },
                )
            }
        }
    }
}