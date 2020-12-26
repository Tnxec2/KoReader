package com.kontranik.koreader.utils

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.kontranik.koreader.R
import com.kontranik.koreader.reader.FontPickerFragment
import com.kontranik.koreader.reader.SettingsActivity
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File
import kotlin.math.max
import kotlin.math.min


/**
 * A [android.preference.Preference] that displays a number picker as a dialog.
 */
class FontPickerPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs), FontPickerFragment.FontPickerDialogListener {

    private var selectedFont: TypefaceRecord = TypefaceRecord.DEFAULT

    private var textSize: Float = PrefsHelper.textSizeMin
    private val textSizeStep: Float = 1F

    private var showSystemFonts: Boolean = false
    private var showNotoFonts: Boolean = false

    private var value = 0

    private var textViewFontName: TextView? = null

    private var fontname: String = TypefaceRecord.DEFAULT.name
    private var fontpath: String ? = null

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.itemView.isClickable = false // disable parent click

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        showSystemFonts = prefs.getBoolean(PrefsHelper.PREF_KEY_USE_SYSTEM_FONTS, false)
        showNotoFonts = prefs.getBoolean(PrefsHelper.PREF_KEY_SHOW_NOTO_FONTS, false)

        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Need read permisson for load fonts from storage", Toast.LENGTH_LONG)
        }

        textViewFontName = holder.findViewById(R.id.textView_preference_font_fontname) as TextView?

        val defaultTextSize = context.resources.getDimension(R.dimen.text_size)

        textSize = sharedPreferences.getFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, defaultTextSize)
        fontpath = sharedPreferences.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH, null)
        fontname = sharedPreferences.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME, TypefaceRecord.DEFAULT.name)!!

        selectedFont = if ( fontpath != null)
            TypefaceRecord(fontname, File(fontpath!!))
        else
            TypefaceRecord(fontname)

        textViewFontName!!.typeface = selectedFont.getTypeface()
        textViewFontName!!.textSize = textSize
        textViewFontName!!.text = fontname

        textViewFontName!!.setOnClickListener {
            val fragment = FontPickerFragment.newInstance(textSize, selectedFont)
            fragment.setCallBack(this)
            val a = context as SettingsActivity
            a.supportFragmentManager.beginTransaction()
                    .replace(R.id.settings_container, fragment)
                    .addToBackStack(null)
                    .commit()
        }


        val decrease = holder.findViewById(R.id.imageView_preference_font_textSizeDecrease) as ImageButton?
        decrease!!.setOnClickListener {
            decreaseTextSize()
        }
        val increase = holder.findViewById(R.id.imageView_preference_font_textSizeIncrease) as ImageButton?
        increase!!.setOnClickListener {
            increaseTextSize()
        }

    }

    private fun decreaseTextSize() {
        textSize = max(PrefsHelper.textSizeMin, textSize - textSizeStep)
        updateTextSize()
    }

    private fun increaseTextSize() {
        textSize = min(PrefsHelper.textSizeMax, textSize + textSizeStep)
        updateTextSize()
    }

    private fun updateTextSize() {
        textViewFontName!!.textSize = textSize
        val editor = sharedPreferences.edit()
        editor.putFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, textSize)
        editor.apply()
    }

    override fun onSaveFontPickerDialog(font: TypefaceRecord?) {
        if ( font != null ) {
            selectedFont = font
            textViewFontName!!.text = font.name
            val editor = sharedPreferences.edit()
            editor.putString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH, selectedFont.file?.absolutePath)
            editor.putString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME, selectedFont.name)
            editor.apply()
        }
    }

    init {
        widgetLayoutResource = R.layout.preference_font_name
    }

}