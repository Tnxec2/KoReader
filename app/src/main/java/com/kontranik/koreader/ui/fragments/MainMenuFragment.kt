package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.databinding.FragmentMainMenuBinding
import com.kontranik.koreader.utils.PrefsHelper

class MainMenuFragment : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButtonMainMenuBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.llMainMenuOpenFile.setOnClickListener {
            openFile()
        }

        binding.llMainMenuLastOpened.setOnClickListener {
            openLastOpened()
        }

        binding.llMainMenuLibrary.setOnClickListener {
            openLibrary()
        }

        binding.llMainMenuSettings.setOnClickListener {
            settings()
        }

        var bookPath: String? = null
        val prefs = requireActivity().getSharedPreferences(ReaderActivity.PREFS_FILE, AppCompatActivity.MODE_PRIVATE)

        if ( prefs.contains(PrefsHelper.PREF_BOOK_PATH) ) {
            bookPath = prefs.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        binding.imageButtonMainmenuBookinfo.setOnClickListener {
            openBookInfo(bookPath)
        }
        if ( bookPath == null) binding.imageButtonMainmenuBookinfo.visibility = View.GONE

    }

    private fun openFile() {
        val fragment = FileChooseFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, "fragment_open_file")
            .addToBackStack("fragment_open_file")
            .commit()
    }

    private fun settings() {
        val fragment = SettingsFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, "fragment_settings")
            .addToBackStack("fragment_settings")
            .commit()
    }

    private fun openLastOpened() {
        val fragment = BookListFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, "fragment_last_opened")
            .addToBackStack("fragment_last_opened")
            .commit()
    }

    private fun openLibrary() {
        val fragment = LibraryMainMenuFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, "fragment_library")
            .addToBackStack("fragment_library")
            .commit()
    }

    private fun openBookInfo(bookUri: String?) {
        if ( bookUri != null) {
            val bookInfoFragment: BookInfoFragment = BookInfoFragment.newInstance(bookUri)
            bookInfoFragment.show(requireActivity().supportFragmentManager, "fragment_bookinfo")
        }
    }

}