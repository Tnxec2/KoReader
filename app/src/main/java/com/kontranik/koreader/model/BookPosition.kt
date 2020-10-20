package com.kontranik.koreader.model

class BookPosition(
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
}