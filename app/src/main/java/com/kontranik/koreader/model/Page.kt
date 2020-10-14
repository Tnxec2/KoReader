package com.kontranik.koreader.model

import java.util.*

class Page (
        var content: CharSequence? = null,
        var startCursor: Cursor = Cursor(),
        var endCursor: Cursor = Cursor()
) {

}