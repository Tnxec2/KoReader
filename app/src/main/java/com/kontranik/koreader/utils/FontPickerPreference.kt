package com.kontranik.koreader.utils

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


/**
 * A [android.preference.Preference] that displays a number picker as a dialog.
 */
class FontPickerPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs), FontPickerFragment.FontPickerDialogListener {

    private var selectedFont: TypefaceRecord = TypefaceRecord.DEFAULT

    private var showSystemFonts: Boolean = false
    private var showNotoFonts: Boolean = false

    private var value = 0

    private var textViewFontName: TextView? = null

    private var fontname: String = TypefaceRecord.DEFAULT.name
    private var fontpath: String ? = null

    private val fonttype = TextType.fromString(attrs.getAttributeValue(null, "fonttype"))

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
        var textSize: Float = sharedPreferences.getFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, defaultTextSize)

        fontpath = sharedPreferences.getString(getFontPathPref(), null)
        fontname = sharedPreferences.getString(getFontNamePref(), TypefaceRecord.DEFAULT.name)!!

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
    }

    override fun onSaveFontPickerDialog(font: TypefaceRecord?) {
        if ( font != null ) {
            selectedFont = font
            textViewFontName!!.text = font.name
            val editor = sharedPreferences.edit()

            editor.putString(getFontPathPref(), selectedFont.file?.absolutePath)
            editor.putString(getFontNamePref(), selectedFont.name)
            editor.apply()
        }
    }

    private fun getFontPathPref(): String {
        return when (fonttype) {
            TextType.Monospace -> {
                PrefsHelper.PREF_KEY_BOOK_FONT_PATH_MONOSPACE
            }
            TextType.Bold -> {
                PrefsHelper.PREF_KEY_BOOK_FONT_PATH_BOLD
            }
            TextType.Italic -> {
                PrefsHelper.PREF_KEY_BOOK_FONT_PATH_ITALIC
            }
            TextType.BoldItalic -> {
                PrefsHelper.PREF_KEY_BOOK_FONT_PATH_BOLDITALIC
            }
            else -> {
                PrefsHelper.PREF_KEY_BOOK_FONT_PATH_NORMAL
            }
        }
    }

    private fun getFontNamePref(): String {
        return when (fonttype) {
            TextType.Monospace -> {
                PrefsHelper.PREF_KEY_BOOK_FONT_NAME_MONOSPACE
            }
            TextType.Bold -> {
                PrefsHelper.PREF_KEY_BOOK_FONT_NAME_BOLD
            }
            TextType.Italic -> {
                PrefsHelper.PREF_KEY_BOOK_FONT_NAME_ITALIC
            }
            TextType.BoldItalic -> {
                PrefsHelper.PREF_KEY_BOOK_FONT_NAME_BOLDITALIC
            }
            else -> {
                PrefsHelper.PREF_KEY_BOOK_FONT_NAME_NORMAL
            }
        }
    }

    init {
        widgetLayoutResource = R.layout.preference_font_name
    }

}

enum class TextType {
    Bold,
    Italic,
    BoldItalic,
    Normal,
    Monospace;

    companion object {
        fun fromString(s: String): TextType {
            return when (s) {
                "monospace", "mono" -> {
                    Monospace
                }
                "bold" -> {
                    Bold
                }
                "italic" -> {
                    Italic
                }
                "bolditalic" -> {
                    BoldItalic
                }
                else -> {
                    Normal
                }
            }
        }
    }
}