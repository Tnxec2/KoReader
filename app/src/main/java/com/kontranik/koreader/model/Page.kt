package com.kontranik.koreader.model

import android.text.SpannableStringBuilder
import java.util.*

class Page (
        var content: SpannableStringBuilder? = null,
        var startCursor: Cursor = Cursor(),
        var endCursor: Cursor = Cursor()
) {

}