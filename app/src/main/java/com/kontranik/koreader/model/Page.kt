package com.kontranik.koreader.model

import android.text.SpannableStringBuilder
import java.util.*

class Page (
        var content: SpannableStringBuilder? = null,
        var startCursor: Cursor = Cursor(),
        var endCursor: Cursor = Cursor()
) {

    constructor(other: Page) : this() {
        if ( other != null) {
            this.content = other.content
            this.startCursor = Cursor(other.startCursor)
            this.endCursor = Cursor(other.endCursor)
        }
    }

}