package com.kontranik.koreader.model

import android.text.SpannableStringBuilder

class Page (
        var content: SpannableStringBuilder? = null,
        var startBookPosition: BookPosition = BookPosition(),
        var endBookPosition: BookPosition = BookPosition()
) {

    constructor(other: Page) : this() {
        if ( other != null) {
            this.content = other.content
            this.startBookPosition = BookPosition(other.startBookPosition)
            this.endBookPosition = BookPosition(other.endBookPosition)
        }
    }

}