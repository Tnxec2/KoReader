package com.kontranik.koreader.model

import android.text.SpannableStringBuilder

class Page (
        var content: SpannableStringBuilder? = null,
        var startBookPosition: BookPosition = BookPosition(),
        var endBookPosition: BookPosition = BookPosition()
) {

    constructor(other: Page) : this() {
        content = other.content
        startBookPosition = BookPosition(other.startBookPosition)
        endBookPosition = BookPosition(other.endBookPosition)
    }

}