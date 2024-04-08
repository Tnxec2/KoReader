package com.kontranik.koreader.ui.components

import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.LineHeightSpan
import android.text.style.URLSpan
import android.view.View
import com.kontranik.koreader.opds.model.Link

class OpdsLinkSpan(val link: Link, val listener: OpdsLinkOnClickListener)
    : SpannableString(
    link.getTitle()
), LineHeightSpan  {
    val height = 10

    init {
        setSpan(CustomUrlSpan(link, listener), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        v: Int,
        fm: Paint.FontMetricsInt?
    ) {
        fm?.let {
            it.bottom += height
            it.descent += height
        }
    }
}

interface OpdsLinkOnClickListener {
    fun onClick(link: Link)
}

class CustomUrlSpan(val link: Link, val listener: OpdsLinkOnClickListener): URLSpan(link.href) {
    override fun onClick(p0: View) {
        listener.onClick(link)
    }
}