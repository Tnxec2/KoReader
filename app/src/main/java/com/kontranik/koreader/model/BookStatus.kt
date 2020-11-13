package com.kontranik.koreader.model

import java.util.*

class BookStatus(
        var id: Long? = null,
        var path: String? = null,
        var position_page: Int = 0,
        var position_offset: Int = 0,
        var lastOpenTime: Long? = null
) {
    init {
        if ( lastOpenTime == null ) updateLastOpenTime()
    }

    fun updatePosition(p: BookPosition) {
        position_page = p.section
        position_offset = p.offSet
    }

    fun updateLastOpenTime() {
        lastOpenTime = Date().time
    }
}