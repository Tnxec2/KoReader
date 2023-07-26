package com.kontranik.koreader.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.databinding.FragmentMainMenuBinding
import com.kontranik.koreader.utils.PrefsHelper

class MainMenuFragment : DialogFragment() {

    private lateinit var binding: FragmentMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButtonMainMenuBack.setOnClickListener {
            dismiss()
        }

        binding.llMainMenuOpenFile.setOnClickListener {
            openFile()
        }

        binding.llMainMenuLastOpened.setOnClickListener {
            openLastOpened()
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

    private var resultLauncherMainMenu = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intentData: Intent? = result.data

            if (intentData != null && intentData.hasExtra(ReaderActivity.PREF_TYPE)) {
                when (intentData.getIntExtra(ReaderActivity.PREF_TYPE, 0)) {
                    ReaderActivity.PREF_TYPE_OPEN_BOOK -> {
                        dismiss()
                    }
                }
            }
        }
    }

    private fun openFile() {
        val fragment = FileChooseFragment()
        fragment.show(requireActivity().supportFragmentManager, "fragment_open_file")
    }

    private fun settings() {
        val fragment = SettingsFragment()
        fragment.show(requireActivity().supportFragmentManager, "fragment_settings")
    }

    private fun openLastOpened() {
        val fragment = BookListFragment()
        fragment.show(requireActivity().supportFragmentManager, "fragment_booklist")
    }

    private fun openBookInfo(bookUri: String?) {
        if ( bookUri != null) {
            val bookInfoFragment: BookInfoFragment = BookInfoFragment.newInstance(bookUri)
            bookInfoFragment.show(requireActivity().supportFragmentManager, "fragment_bookinfo")
        }
    }

}