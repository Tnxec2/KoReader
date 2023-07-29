package com.kontranik.koreader.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.databinding.FragmentMainMenuBinding
import com.kontranik.koreader.utils.PrefsHelper

class MainMenuFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.imageButtonMainMenuBack.setOnClickListener {
//            dismiss()
//        }

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
        fragment.show(requireActivity().supportFragmentManager, "fragment_open_file")

        requireActivity().supportFragmentManager.setFragmentResultListener("open_file", this) { key, _ ->
            if (key == "open_file") {
                dismiss()
            }
        }
    }

    private fun settings() {
        val fragment = SettingsFragment()
        fragment.show(requireActivity().supportFragmentManager, "fragment_settings")
    }

    private fun openLastOpened() {
        val fragment = BookListFragment()
        fragment.show(requireActivity().supportFragmentManager, "fragment_booklist")
    }

    private fun openLibrary() {
        val fragment = LibraryMainMenuFragment()
        fragment.show(requireActivity().supportFragmentManager, "fragment_library")
    }

    private fun openBookInfo(bookUri: String?) {
        if ( bookUri != null) {
            val bookInfoFragment: BookInfoFragment = BookInfoFragment.newInstance(bookUri)

            bookInfoFragment.show(requireActivity().supportFragmentManager, "fragment_bookinfo")
        }
    }

}