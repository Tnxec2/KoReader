package com.kontranik.koreader.model

import java.util.*

class BookStatus(
        var id: Long? = null,
        var path: String? = null,
        var title: String? = null,
        var authors: String? = null,
        var position_section: Int = 0,
        var position_offset: Int = 0,
        var lastOpenTime: Long? = null
) {
    constructor(book: Book) : this(
        id = null,
        path = book.fileLocation,
        title = book.ebookHelper?.bookInfo?.title,
        authors = book.ebookHelper?.bookInfo?.authorsAsString(),
        position_section = if ( book.curPage == null) 0 else book.curPage!!.startBookPosition.section,
        position_offset = if ( book.curPage == null) 0 else book.curPage!!.startBookPosition.offSet
    )

    init {
        if ( lastOpenTime == null ) updateLastOpenTime()
    }

    fun updatePosition(p: BookPosition) {
        position_section = p.section
        position_offset = p.offSet
    }

    fun updateLastOpenTime() {
        lastOpenTime = Date().time
    }
}