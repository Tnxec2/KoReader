package com.kontranik.koreader.reader

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.rarepebble.colorpicker.ColorPreference




class SettingsActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    var close: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        close = findViewById(R.id.imageButton_settings_back)

        close!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24))

        close!!.setOnClickListener {
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
                    close!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24))
                } else {
                    close!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24))
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
        override fun onDisplayPreferenceDialog(preference: Preference?) {
            if (preference is ColorPreference) {
                preference.showDialog(this, 0)
            } else super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onPreferenceStartFragment(
            caller: PreferenceFragmentCompat?,
            pref: Preference?): Boolean {
        // Instantiate the new Fragment
        val args = pref!!.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                pref.fragment)
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