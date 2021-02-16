package com.kontranik.koreader.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.preference.PreferenceManager
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord


class FontTextView : AppCompatTextView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {}

    private var typefaceNormal: Typeface
    private var typefaceBold: Typeface
    private var typefaceItalic: Typeface
    private var typefaceBoldItalic: Typeface
    private var typefaceMonospace: Typeface

    init {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        var fontPath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_NORMAL, null)
        var fontName = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_NORMAL, TypefaceRecord.DEFAULT.name)!!
        typefaceNormal = if ( fontPath != null)
            Typeface.createFromFile(fontPath)
        else
            Typeface.create(fontName, Typeface.NORMAL)

        fontPath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_BOLD, null)
        fontName = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_BOLD, TypefaceRecord.DEFAULT.name)!!
        typefaceBold = if ( fontPath != null)
            Typeface.createFromFile(fontPath)
        else
            Typeface.create(fontName, Typeface.NORMAL)

        fontPath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_ITALIC, null)
        fontName = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_ITALIC, TypefaceRecord.DEFAULT.name)!!
        typefaceItalic = if ( fontPath != null)
            Typeface.createFromFile(fontPath)
        else
            Typeface.create(fontName, Typeface.NORMAL)

        fontPath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_BOLDITALIC, null)
        fontName = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_BOLDITALIC, TypefaceRecord.DEFAULT.name)!!
        typefaceBoldItalic = if ( fontPath != null)
            Typeface.createFromFile(fontPath)
        else
            Typeface.create(fontName, Typeface.NORMAL)

        fontPath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_MONOSPACE, null)
        fontName = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_MONOSPACE, TypefaceRecord.MONO)!!
        typefaceMonospace = if ( fontPath != null)
            Typeface.createFromFile(fontPath)
        else
            Typeface.create(fontName, Typeface.NORMAL)

    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setText(text: CharSequence, type: BufferType) {
        var spannable = replaceStyledSpans(SpannableString(text))
        spannable = replaceMonospace(spannable)
        super.setText(spannable, type)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun replaceMonospace(spannable: SpannableString): SpannableString {
        val allSpans = spannable.getSpans(0, spannable.length, TypefaceSpan::class.java)
        for (typfaceSpan in allSpans) {
            Log.d("FontTextView", typfaceSpan.family.toString())
            if ( typfaceSpan.family == "monospace" || typfaceSpan.typeface == Typeface.MONOSPACE) {
                val start: Int = spannable.getSpanStart(typfaceSpan)
                val end: Int = spannable.getSpanEnd(typfaceSpan)
                spannable.removeSpan(typfaceSpan)
                spannable.setSpan(TypefaceSpan(typefaceMonospace), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return spannable
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun replaceStyledSpans(spannable:SpannableString): SpannableString {
        val allSpans = spannable.getSpans(0, text.length, StyleSpan::class.java)

        spannable.setSpan(TypefaceSpan(typefaceNormal), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        for ( styleSpan in allSpans) {

            val start: Int = spannable.getSpanStart(styleSpan)
            val end: Int = spannable.getSpanEnd(styleSpan)

            if ( start < 0) continue

            val spans = spannable.getSpans(start, end, StyleSpan::class.java)

            var hasBold = false
            var hasItalic = false
            for (sp in spans) {
                if (sp.style == Typeface.BOLD) hasBold = true
                if (sp.style == Typeface.ITALIC) hasItalic = true
                if (sp.style == Typeface.BOLD_ITALIC) {
                    hasBold = true
                    hasItalic = true
                }
                spannable.removeSpan(sp)
            }

            if (hasBold && hasItalic)
                spannable.setSpan(TypefaceSpan(typefaceBoldItalic), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            else if (hasBold)
                spannable.setSpan(TypefaceSpan(typefaceBold), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            else if (hasItalic)
                spannable.setSpan(TypefaceSpan(typefaceItalic), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            else
                spannable.setSpan(TypefaceSpan(typefaceNormal), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        }
        return spannable
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}