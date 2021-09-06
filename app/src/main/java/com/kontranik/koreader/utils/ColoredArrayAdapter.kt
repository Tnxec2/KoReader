package com.kontranik.koreader.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup

import android.widget.ArrayAdapter
import android.widget.TextView

import androidx.preference.PreferenceManager

class ColoredArrayAdapter(context: Context, textViewResourceId: Int, data: List<String>, val textSize: Float, val typeface: Typeface) : ArrayAdapter<String?>(context, textViewResourceId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = super.getView(position, convertView, parent)

        val prefsHelper = PrefsHelper(context)

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        var co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_BACK + (position + 1), 0)
        val colorBack = if ( co != 0) "#" + Integer.toHexString(co) else prefsHelper.colorBackDefault

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_TEXT + (position + 1), 0)
        val colorText = if ( co != 0 ) "#" + Integer.toHexString(co) else prefsHelper.colorTextDefault

        view.setBackgroundColor(Color.parseColor(colorBack))
        (view as TextView).setTextColor(Color.parseColor(colorText))
        view.textSize = textSize
        view.typeface = typeface


        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = super.getView(position, convertView, parent)

        val prefsHelper = PrefsHelper(context)

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        Log.d("TEST", "" + position)

        var co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_BACK + (position + 1), 0)
        val colorBack = if ( co != 0) "#" + Integer.toHexString(co) else prefsHelper.colorBackDefault

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_TEXT + (position + 1), 0)
        val colorText = if ( co != 0 ) "#" + Integer.toHexString(co) else prefsHelper.colorTextDefault

        view.setBackgroundColor(Color.parseColor(colorBack))
        (view as TextView).setTextColor(Color.parseColor(colorText))
        view.textSize = textSize
        view.typeface = typeface

        return view
    }
}