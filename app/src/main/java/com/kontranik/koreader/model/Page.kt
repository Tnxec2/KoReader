package com.kontranik.koreader.model

import android.text.SpannableStringBuilder

data class Page (
        var content: SpannableStringBuilder? = null,
        var startBookPosition: BookPosition = BookPosition(),
        var endBookPosition: BookPosition = BookPosition()
)