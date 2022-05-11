package com.kontranik.koreader.ui.adapters

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.kontranik.koreader.utils.PrefsHelper

class ColoredArrayAdapter(
    context: Context,
    textViewResourceId: Int,
    data: List<String>,
    val textSize: Float,
    val typeface: Typeface
) : ArrayAdapter<String?>(context, textViewResourceId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = super.getView(position, convertView, parent)

        setupView(view, position)

        return view
    }

    private fun setupView(view: View, position: Int) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        var co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_BACK + (position + 1), 0)
        val colorBack = if (co != 0) "#" + Integer.toHexString(co)
        else context.resources.getString(
            PrefsHelper.colorBackgroundDefaultArray[position]
        )

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_TEXT + (position + 1), 0)
        val colorText = if (co != 0) "#" + Integer.toHexString(co)
        else context.resources.getString(
            PrefsHelper.colorForegroundDefaultArray[position]
        )

        view.setBackgroundColor(Color.parseColor(colorBack))
        (view as TextView).setTextColor(Color.parseColor(colorText))
        view.textSize = textSize
        view.typeface = typeface
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = super.getView(position, convertView, parent)

        setupView(view, position)

        return view
    }


}