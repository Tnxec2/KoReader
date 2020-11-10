package com.kontranik.koreader.model

import java.util.*

class BookStatus(
        var id: Long? = null,
        var path: String? = null,
        var position_page: Int = 0,
        var position_element: Int = 0,
        var position_paragraph: Int = 0,
        var position_symbol: Int = 0,
        var lastOpenTime: Long? = null
) {
    init {
        if ( lastOpenTime == null ) updateLastOpenTime()
    }

    fun updatePosition(p: BookPosition) {
        position_page = p.page
        position_element = p.element
        position_paragraph = p.paragraph
        position_symbol = p.symbol
    }

    fun updateLastOpenTime() {
        lastOpenTime = Date().time
    }
}