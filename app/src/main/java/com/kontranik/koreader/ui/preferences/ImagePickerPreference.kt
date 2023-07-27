package com.kontranik.koreader.ui.preferences

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.DialogPreference

import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.PrefsHelper
import java.lang.Exception


class ImagePickerPreference(context: Context, attrs: AttributeSet) :
    DialogPreference(context, attrs) {

    private val themeId = attrs.getAttributeValue(null, "themeid")

    private var textViewImageUri: TextView? = null

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        textViewImageUri = holder. findViewById(R.id.textView_preference_imagepicker_imageuri) as TextView?
        if ( textViewImageUri != null) {

            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val imageUri = prefs.getString(PrefsHelper.PREF_KEY_BACKGROUND_IMAGE_URI + themeId, null)

            setImageUri(imageUri)
        }
    }

    fun setImageUri(imageUri: String?) {

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString(PrefsHelper.PREF_KEY_BACKGROUND_IMAGE_URI + themeId, imageUri)
        editor.apply()

        var fileName = ""
        if ( imageUri == null) {
            textViewImageUri!!.text = ""
            return
        }
        val uri = Uri.parse(imageUri)
        try {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            uri?.let {
                if (it.scheme.equals("file")) {
                    fileName = it.lastPathSegment.toString()
                } else {
                    var cursor: Cursor? = null
                    try {
                        cursor = context.contentResolver.query(it, arrayOf(
                                MediaStore.Images.ImageColumns.DISPLAY_NAME
                        ), null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                            if (index >= 0) fileName = cursor.getString(index)
                        }
                    } finally {
                        cursor?.close()
                    }
                }
            }
            textViewImageUri!!.text = fileName
        } catch (_: Exception) {

        }
    }

    init {
        widgetLayoutResource = R.layout.preference_image_picker
    }
}