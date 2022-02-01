package com.kontranik.koreader.reader

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.databinding.ActivitySettingsBinding
import com.kontranik.koreader.utils.ImagePickerPreference
import com.rarepebble.colorpicker.ColorPreference


class SettingsActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private lateinit var binding: ActivitySettingsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageButtonSettingsBack.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24))

        binding.imageButtonSettingsBack.setOnClickListener {
            if (!supportFragmentManager.popBackStackImmediate()) {
                val data = Intent()
                data.putExtra(ReaderActivity.PREF_TYPE, ReaderActivity.PREF_TYPE_SETTINGS)
                setResult(RESULT_OK, data)
                finish()
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.addOnBackStackChangedListener {
                if (supportFragmentManager.backStackEntryCount == 0) {
                    binding.imageButtonSettingsBack.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24))
                } else {
                    binding.imageButtonSettingsBack.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24))
                }
            }
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings_container, RootSettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


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

    open class ColorThemeGeneralSettingsFragment(val themeId: Int) : PreferenceFragmentCompat() {
        var imageUri = ""
        var preference: Preference? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            if ( themeId == 1)
                setPreferencesFromResource(R.xml.color_theme1_preferences, rootKey)
            else if ( themeId == 2)
                setPreferencesFromResource(R.xml.color_theme2_preferences, rootKey)
            else if ( themeId == 3)
                setPreferencesFromResource(R.xml.color_theme3_preferences, rootKey)
            else if ( themeId == 4)
                setPreferencesFromResource(R.xml.color_theme4_preferences, rootKey)
            else if ( themeId == 5)
                setPreferencesFromResource(R.xml.color_theme5_preferences, rootKey)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == RESULT_OK && requestCode == ImagePickerPreference.PICK_IMAGE) {
                imageUri = data!!.data.toString()
                findPreference<ImagePickerPreference>("backgroundImageTheme$themeId")?.setImageUri(imageUri)
            }
        }

        override fun onDisplayPreferenceDialog(preference: Preference) {
            if (preference is ColorPreference) {
                preference.showDialog(this, 0)
            } else if ( preference is ImagePickerPreference) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_OPEN_DOCUMENT
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivityForResult(Intent.createChooser(intent, "Select backgroud image"), ImagePickerPreference.PICK_IMAGE)
            } else {
                super.onDisplayPreferenceDialog(preference)
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
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                pref.fragment!!)
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, fragment)
                .addToBackStack(null)
                .commit()
        return true
    }
}