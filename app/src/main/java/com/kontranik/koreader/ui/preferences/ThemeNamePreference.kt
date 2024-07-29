package com.kontranik.koreader.ui.preferences

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.kontranik.koreader.R
import com.kontranik.koreader.model.PageViewColorSettings
import com.kontranik.koreader.utils.PrefsHelper
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File
import java.io.InputStream


class ThemeNamePreference(context: Context, attrs: AttributeSet) :
    Preference(context, attrs)  {

    private var value = 0

    private val colorTheme = attrs.getAttributeValue(null, "colorTheme") ?: PrefsHelper.PREF_COLOR_SELECTED_THEME_DEFAULT.toString()

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val textViewThemeName = holder.findViewById(R.id.textView_preference_theme_themename) as TextView?


        var co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_BACK + colorTheme, 0)
        val colorBack =
            if (co != 0) "#" + Integer.toHexString(co)
            else context.resources.getString(PrefsHelper.colorBackgroundDefaultArray[colorTheme.toInt()-1])

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_TEXT + colorTheme, 0)
        val colorText =
            if (co != 0) "#" + Integer.toHexString(co)
            else context.resources.getString(PrefsHelper.colorForegroundDefaultArray[colorTheme.toInt()-1])

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_LINKTEXT + colorTheme, 0)
        val colorLinkText =
            if (co != 0) "#" + Integer.toHexString(co)
            else context.resources.getString(PrefsHelper.colorLinkDefaultArray[colorTheme.toInt()-1])

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_INFOTEXT + colorTheme, 0)
        val colorInfoText =
            if (co != 0) "#" + Integer.toHexString(co)
            else context.resources.getString(PrefsHelper.colorInfotextDefaultArray[colorTheme.toInt()-1])

        val showBackgroundImage = prefs.getBoolean(
            PrefsHelper.PREF_KEY_SHOW_BACKGROUND_IMAGE + colorTheme,
            false
        )

        val backgroundImageUri = prefs.getString(
            PrefsHelper.PREF_KEY_BACKGROUND_IMAGE_URI + colorTheme,
            null
        )

        val backgroundImageTiledRepeat = prefs.getBoolean(
            PrefsHelper.PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT + colorTheme,
            false
        )

        val colorSettings = PageViewColorSettings(
            showBackgroundImage = showBackgroundImage,
            backgroundImageUri = backgroundImageUri,
            backgroundImageTiledRepeat = backgroundImageTiledRepeat,
            colorText = colorText,
            colorBack = colorBack,
            colorLink = colorLinkText,
            colorInfoText = colorInfoText,
        )

        val fontpath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_NORMAL, null)

        val defaultTextSize = context.resources.getDimension(R.dimen.text_size)
        val textSize = prefs.getFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, defaultTextSize)
        val fontname = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_NORMAL, TypefaceRecord.DEFAULT.name)!!

        textViewThemeName!!.textSize = textSize

        val selectedFont = if ( fontpath != null ) {
            val f = File(fontpath)
            if (f.exists() && f.isFile && f.canRead())
                TypefaceRecord(fontname, f)
            else
                TypefaceRecord.DEFAULT
        } else {
            TypefaceRecord(fontname)
        }
        textViewThemeName.typeface = selectedFont.getTypeface()

        textViewThemeName.text = context.resources.getString(R.string.example_abcabc)

        setColorTheme(colorSettings, textViewThemeName)
    }

    private fun setColorTheme(colorSettings: PageViewColorSettings, textView: TextView) {
        if (colorSettings.showBackgroundImage
            && colorSettings.backgroundImageUri != null
            && !colorSettings.backgroundImageUri.equals(
                ""
            )
        ) {
            textView.setBackgroundColor(Color.TRANSPARENT)
            try {
                val uri = Uri.parse(colorSettings.backgroundImageUri)
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapDrawable(context.resources, inputStream)
                if (colorSettings.backgroundImageTiledRepeat)
                    bitmap.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                textView.background = bitmap
            } catch (e: Exception) {
                textView.background = null
                textView.setBackgroundColor(Color.parseColor(colorSettings.colorBack))
            }
        } else {
            textView.background = null
            textView.setBackgroundColor(Color.parseColor(colorSettings.colorBack))
        }

        textView.setTextColor(Color.parseColor(colorSettings.colorText))
        textView.setLinkTextColor(Color.parseColor(colorSettings.colorLink))
    }


    init {
        widgetLayoutResource = R.layout.preference_theme_name
    }

}

