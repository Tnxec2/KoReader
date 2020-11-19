package com.kontranik.koreader.reader

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val close = findViewById<ImageButton>(R.id.imageButton_settings_back)
        close.setOnClickListener {
            val data = Intent()
            data.putExtra(ReaderActivity.PREF_TYPE, ReaderActivity.PREF_TYPE_SETTINGS)
            setResult(RESULT_OK, data)
            finish()
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}