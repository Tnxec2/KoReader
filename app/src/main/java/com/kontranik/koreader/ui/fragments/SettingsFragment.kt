package com.kontranik.koreader.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.databinding.FragmentSettingsBinding
import com.kontranik.koreader.ui.preferences.ImagePickerPreference
import com.rarepebble.colorpicker.ColorPreference


class SettingsFragment : DialogFragment(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mReaderActivityViewModel = ViewModelProvider(requireActivity())[ReaderActivityViewModel::class.java]

        binding.imageButtonSettingsBack.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_close_24))

        binding.imageButtonSettingsBack.setOnClickListener {
            if (childFragmentManager.backStackEntryCount == 0) {
                mReaderActivityViewModel.loadSettings(requireActivity())
                dismiss()
            } else {
                childFragmentManager.popBackStack()
            }
        }

        if (savedInstanceState == null) {
            childFragmentManager.addOnBackStackChangedListener {
                if (childFragmentManager.backStackEntryCount == 0) {
                    binding.imageButtonSettingsBack.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_close_24))
                } else {
                    binding.imageButtonSettingsBack.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_arrow_back_24))
                }
            }
            childFragmentManager
                    .beginTransaction()
                    .add(binding.settingsContainer.id, RootSettingsFragment())
                    .commit()
        }
    }

    class RootSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    class InterfaceSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.interface_preferences, rootKey)
        }
    }
    class TapZonesSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.tapzone_preferences, rootKey)
        }
    }
    class TapZoneOneSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.tapzone_one_preferences, rootKey)
        }
    }
    class TapZoneDoubleSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.tapzone_double_preferences, rootKey)
        }
    }

    class TapZoneLongSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.tapzone_long_preferences, rootKey)
        }
    }

    class TextSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.text_preferences, rootKey)
        }
    }

    class ColorSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.color_preferences, rootKey)
        }
    }

    open class ColorThemeGeneralSettingsFragment(private val themeId: Int) : PreferenceFragmentCompat() {
        private var imageUri = ""
        var preference: Preference? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            when (themeId) {
                1 -> setPreferencesFromResource(R.xml.color_theme1_preferences, rootKey)
                2 -> setPreferencesFromResource(R.xml.color_theme2_preferences, rootKey)
                3 -> setPreferencesFromResource(R.xml.color_theme3_preferences, rootKey)
                4 -> setPreferencesFromResource(R.xml.color_theme4_preferences, rootKey)
                5 -> setPreferencesFromResource(R.xml.color_theme5_preferences, rootKey)
            }
        }

//        @Deprecated("Deprecated in Java")
//        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//            super.onActivityResult(requestCode, resultCode, data)
//            if (resultCode == Activity.RESULT_OK && requestCode == ImagePickerPreference.PICK_IMAGE) {
//                imageUri = data!!.data.toString()
//                findPreference<ImagePickerPreference>("backgroundImageTheme$themeId")?.setImageUri(imageUri)
//            }
//        }

        private val startForResultPickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent
                imageUri = intent.toString()
                findPreference<ImagePickerPreference>("backgroundImageTheme$themeId")?.setImageUri(imageUri)
            }
        }

        override fun onDisplayPreferenceDialog(preference: Preference) {
            when (preference) {
                is ColorPreference -> {
                    preference.showDialog(this, 0)
                }
                is ImagePickerPreference -> {
                    val intent = Intent()
                    with(intent) {
                        type = "image/*"
                        action = Intent.ACTION_OPEN_DOCUMENT
                        addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    //startActivityForResult(Intent.createChooser(intent, "Select backgroud image"), ImagePickerPreference.PICK_IMAGE)
                    startForResultPickImage.launch(Intent.createChooser(intent, "Select backgroud image"))
                }
                else -> {
                    super.onDisplayPreferenceDialog(preference)
                }
            }
        }
    }

    class ColorTheme1SettingsFragment : ColorThemeGeneralSettingsFragment(1) {  }

    class ColorTheme2SettingsFragment : ColorThemeGeneralSettingsFragment(2) {  }

    class ColorTheme3SettingsFragment : ColorThemeGeneralSettingsFragment(3) {  }

    class ColorTheme4SettingsFragment : ColorThemeGeneralSettingsFragment(4) {  }

    class ColorTheme5SettingsFragment : ColorThemeGeneralSettingsFragment(5) {  }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {

        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = parentFragmentManager
            .fragmentFactory
            .instantiate(
            requireActivity().classLoader,
            pref.fragment!!
        )
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)


        // Replace the existing Fragment with the new Fragment

        childFragmentManager.beginTransaction()
            .replace(binding.settingsContainer.id, fragment)
            .addToBackStack(pref.key)
            .commit()


        return true
    }
}