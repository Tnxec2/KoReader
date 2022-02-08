package com.kontranik.koreader.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import com.kontranik.koreader.model.BookFonts
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord

open class FontsHelper(var context: Context) {

    private lateinit var bookFonts: BookFonts

    init {
        loadFonts()
    }

    fun loadFonts() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        var fontPath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_NORMAL, null)
        var fontName = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_NORMAL, TypefaceRecord.DEFAULT.name)!!
        val typefaceNormal = if ( fontPath != null)
            Typeface.createFromFile(fontPath)
        else
            Typeface.create(fontName, Typeface.NORMAL)

        fontPath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_BOLD, null)
        fontName = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_BOLD, TypefaceRecord.DEFAULT.name)!!
        val typefaceBold = if ( fontPath != null)
            Typeface.createFromFile(fontPath)
        else
            Typeface.create(fontName, Typeface.BOLD)

        fontPath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_ITALIC, null)
        fontName = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_ITALIC, TypefaceRecord.DEFAULT.name)!!
        val typefaceItalic = if ( fontPath != null)
            Typeface.createFromFile(fontPath)
        else
            Typeface.create(fontName, Typeface.ITALIC)

        fontPath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_BOLDITALIC, null)
        fontName = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_BOLDITALIC, TypefaceRecord.DEFAULT.name)!!
        val typefaceBoldItalic = if ( fontPath != null)
            Typeface.createFromFile(fontPath)
        else
            Typeface.create(fontName, Typeface.BOLD_ITALIC)

        fontPath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_MONOSPACE, null)
        fontName = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_MONOSPACE, TypefaceRecord.MONO)!!
        val typefaceMonospace = if ( fontPath != null)
            Typeface.createFromFile(fontPath)
        else
            Typeface.create(fontName, Typeface.NORMAL)
        bookFonts = BookFonts(typefaceNormal, typefaceBold, typefaceItalic, typefaceBoldItalic, typefaceMonospace)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    internal fun replaceStyledSpans(spannable: Spannable, length: Int): Spannable {
        val allSpans = spannable.getSpans(0, length, StyleSpan::class.java)

        spannable.setSpan(TypefaceSpan(bookFonts.typefaceNormal), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

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
                spannable.setSpan(TypefaceSpan(bookFonts.typefaceBoldItalic), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            else if (hasBold)
                spannable.setSpan(TypefaceSpan(bookFonts.typefaceBold), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            else if (hasItalic)
                spannable.setSpan(TypefaceSpan(bookFonts.typefaceItalic), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            else
                spannable.setSpan(TypefaceSpan(bookFonts.typefaceNormal), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }

    @RequiresApi(Build.VERSION_CODES.P)
    internal fun replaceTypefaces(spannable: Spannable): Spannable {
        val allSpans = spannable.getSpans(0, spannable.length, TypefaceSpan::class.java)
        for (typfaceSpan in allSpans) {
            Log.d("FontsHelper", "family: ${typfaceSpan.family}, typeface: ${typfaceSpan.typeface?.style}")
            if ( typfaceSpan.family == "monospace" || typfaceSpan.typeface == Typeface.MONOSPACE) {
                val start: Int = spannable.getSpanStart(typfaceSpan)
                val end: Int = spannable.getSpanEnd(typfaceSpan)
                spannable.removeSpan(typfaceSpan)
                spannable.setSpan(TypefaceSpan(bookFonts.typefaceMonospace), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else if ( typfaceSpan.family == "bold" || typfaceSpan.typeface?.style == Typeface.BOLD) {
                val start: Int = spannable.getSpanStart(typfaceSpan)
                val end: Int = spannable.getSpanEnd(typfaceSpan)
                spannable.removeSpan(typfaceSpan)
                spannable.setSpan(TypefaceSpan(bookFonts.typefaceBold), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else if ( typfaceSpan.family == "italic" || typfaceSpan.typeface?.style == Typeface.ITALIC) {
                val start: Int = spannable.getSpanStart(typfaceSpan)
                val end: Int = spannable.getSpanEnd(typfaceSpan)
                spannable.removeSpan(typfaceSpan)
                spannable.setSpan(TypefaceSpan(bookFonts.typefaceItalic), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else if ( typfaceSpan.family == "bold_italic" || typfaceSpan.typeface?.style == Typeface.BOLD_ITALIC) {
                val start: Int = spannable.getSpanStart(typfaceSpan)
                val end: Int = spannable.getSpanEnd(typfaceSpan)
                spannable.removeSpan(typfaceSpan)
                spannable.setSpan(TypefaceSpan(bookFonts.typefaceBoldItalic), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return spannable
    }

}