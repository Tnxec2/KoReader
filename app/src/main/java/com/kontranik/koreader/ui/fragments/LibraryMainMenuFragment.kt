package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentLibraryMainBinding

class LibraryMainMenuFragment : DialogFragment() {

    private lateinit var binding: FragmentLibraryMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLibraryMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButtonLibraryMainBack.setOnClickListener {
            dismiss()
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
        fragment.show(requireActivity().supportFragmentManager, "fragment_library_by_title")
    }

    private fun openByAuthor() {
        // TODO: open library by author
//        val fragment = FileChooseFragment()
//        fragment.show(requireActivity().supportFragmentManager, "fragment_open_file")
//
//        requireActivity().supportFragmentManager.setFragmentResultListener("open_file", this) { key, _ ->
//            if (key == "open_file") {
//                dismiss()
//            }
//        }
    }

    private fun settings() {
        val fragment = LibrarySettingsFragment()
        fragment.show(requireActivity().supportFragmentManager, "fragment_library_settings")
    }


}