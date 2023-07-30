package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentLibraryMainBinding

class LibraryMainMenuFragment : Fragment() {

    private lateinit var binding: FragmentLibraryMainBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLibraryMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButtonLibraryMainBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.llLibraryMainSettings.setOnClickListener {
            settings()
        }

        binding.llLibraryMainByTitle.setOnClickListener {
            openByTitle()
        }

        binding.llLibraryMainByAuthor.setOnClickListener {
            openByAuthor()
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