package com.kontranik.koreader.model

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
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
import com.kontranik.koreader.R

class WordSequence(var text: String, var style: MyStyle, c: Context) : ContextWrapper(c){
    var data: SpannableString
    var wRelativeTextSize: Float
    var wColor: Int
    var wStyle: Int

    init {
        val spannable = SpannableString(text)
        wRelativeTextSize = 1.0f
        wStyle = Typeface.NORMAL
        wColor = c.resources.getColor(R.color.textview_normal)
        val tColor = c.resources.getColor(R.color.textview_title)
        val eColor = c.resources.getColor(R.color.error)
        when (style) {
            MyStyle.Title, MyStyle.H1 -> {
                wRelativeTextSize = 2.6f
                wStyle = Typeface.BOLD_ITALIC
                wColor = tColor
            }
            MyStyle.H2 -> {
                wRelativeTextSize = 1.95f
                wStyle = Typeface.BOLD
                wColor = tColor
            }
            MyStyle.H3 -> {
                wRelativeTextSize = 1.521f
                wStyle = Typeface.BOLD
                wColor = tColor
            }
            MyStyle.H4 -> {
                wRelativeTextSize = 1.2f
                wStyle = Typeface.BOLD
                wColor = tColor
            }
            MyStyle.H5 -> {
                wRelativeTextSize = 1.079f
                wStyle = Typeface.BOLD_ITALIC
                wColor = tColor
            }
            MyStyle.H6 -> {
                wRelativeTextSize = 0.871f
                wStyle = Typeface.BOLD_ITALIC
            }
            MyStyle.Bold -> {
                wStyle = Typeface.BOLD
            }
            MyStyle.Italic -> {
                wStyle = Typeface.ITALIC
            }
            MyStyle.Other -> {
                wStyle = Typeface.BOLD_ITALIC
                wColor = eColor
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