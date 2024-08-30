package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.kontranik.koreader.compose.ui.opds.OpdsListScreen
import com.kontranik.koreader.compose.ui.opds.OpdsViewModell
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.Link


class OpdsEntryListFragment :
    Fragment() {

    lateinit var opdsViewModell: OpdsViewModell

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        opdsViewModell = ViewModelProvider(requireActivity(), AppViewModelProvider.Factory)[OpdsViewModell::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                OpdsListScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = { requireActivity().supportFragmentManager.popBackStack() },
                    opdsViewModell = opdsViewModell
                )
            }
        }
    }


    companion object {
        fun newInstance(): OpdsEntryListFragment {
            val frag = OpdsEntryListFragment()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }
}