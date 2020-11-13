package com.kontranik.koreader.model

open class BookPosition(
        var section: Int = 0,
        var offSet: Int = 0
) {
    constructor(p: BookPosition): this(
            section = p.section,
            offSet=p.offSet
    )

    constructor(bookmark: Bookmark) : this(
            section = bookmark.position_section,
            offSet = bookmark.position_offset
    )
}