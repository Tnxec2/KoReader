package com.kontranik.koreader.model

import android.text.SpannableStringBuilder

data class Page (
        var content: SpannableStringBuilder? = null,
        var pageStartPosition: BookPosition = BookPosition(),
        var pageEndPosition: BookPosition = BookPosition()
)