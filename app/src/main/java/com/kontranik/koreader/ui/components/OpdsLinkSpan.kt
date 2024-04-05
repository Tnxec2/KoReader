package com.kontranik.koreader.ui.components

import android.text.SpannableString
import android.text.Spanned
import android.text.style.URLSpan
import android.view.View
import com.kontranik.koreader.opds.model.Link

class OpdsLinkSpan(val link: Link, val listener: OpdsLinkOnClickListener)
    : SpannableString(
    link.getTitle()
) {

    init {
        setSpan(CustomUrlSpan(link, listener), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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