package com.kontranik.koreader.model

import com.kontranik.koreader.database.model.Bookmark

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

    override fun toString(): String {
        return "BookPosition: seciton = $section, offSet = $offSet"
    }
}