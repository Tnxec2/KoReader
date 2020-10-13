package com.kontranik.koreader.model

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import androidx.annotation.RequiresApi

class Word(var text: String, var style: MyStyle) {
    var data: SpannableString
    var wRelativeTextSize: Float
    var wColor: Int
    var wStyle: Int
    fun getwRelativeTextSize(): Float {
        return wRelativeTextSize
    }

    init {
        val spannable = SpannableString(text)
        wRelativeTextSize = 1.0f
        wStyle = Typeface.NORMAL
        wColor = Color.BLACK
        when (style) {
            MyStyle.Title, MyStyle.H1 -> {
                wRelativeTextSize = 2.6f
                wStyle = Typeface.BOLD_ITALIC
                wColor = Color.MAGENTA
            }
            MyStyle.H2 -> {
                wRelativeTextSize = 1.95f
                wStyle = Typeface.BOLD
                wColor = Color.MAGENTA
            }
            MyStyle.H3 -> {
                wRelativeTextSize = 1.521f
                wStyle = Typeface.BOLD
                wColor = Color.MAGENTA
            }
            MyStyle.H4 -> {
                wRelativeTextSize = 1.2f
                wStyle = Typeface.BOLD
                wColor = Color.MAGENTA
            }
            MyStyle.H5 -> {
                wRelativeTextSize = 1.079f
                wStyle = Typeface.BOLD_ITALIC
                wColor = Color.MAGENTA
            }
            MyStyle.H6 -> {
                wRelativeTextSize = 0.871f
                wStyle = Typeface.BOLD_ITALIC
            }
            MyStyle.Bold -> {
                wStyle = Typeface.BOLD
            }
            else -> {
            }
        }
       // wRelativeTextSize = 1f
       // wTypeFace = Typeface.NORMAL
       // wColor = Color.BLACK
        spannable.setSpan(RelativeSizeSpan(wRelativeTextSize), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(wStyle), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(wColor), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        data = spannable
    }
}