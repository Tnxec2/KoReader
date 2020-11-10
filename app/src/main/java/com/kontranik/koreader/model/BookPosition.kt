package com.kontranik.koreader.model

open class BookPosition(
        var page: Int = 0,
        var element: Int = 0,
        var paragraph: Int = 0,
        var symbol: Int = 0
) {
    constructor(p: BookPosition): this(
            page = p.page,
            element=p.element,
            paragraph = p.paragraph,
            symbol=p.symbol)

    constructor(bookmark: Bookmark) : this(
            page = bookmark.position_page,
            element = bookmark.position_element,
            paragraph = bookmark.position_paragraph,
            symbol = bookmark.position_symbol
    )
}