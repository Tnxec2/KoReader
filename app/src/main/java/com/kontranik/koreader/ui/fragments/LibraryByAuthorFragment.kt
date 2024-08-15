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
import com.kontranik.koreader.compose.ui.library.byauthor.LibraryByAuthorScreen
import com.kontranik.koreader.database.model.Author


open class LibraryByAuthorFragment : Fragment()  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LibraryByAuthorScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    navigateBack = {
                        requireActivity().supportFragmentManager.popBackStack()
                    },
                    navigateToAuthor = {
                        onClickAuthorItem(it)
                    }
                )
            }
        }
    }

    private fun onClickAuthorItem(author: Author) {
        val fragment = LibraryByTitleFragment.newInstance(author)
        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container_view, fragment, "fragment_library_by_title")
            .addToBackStack("fragment_library_by_title")
            .commit()
    }
}