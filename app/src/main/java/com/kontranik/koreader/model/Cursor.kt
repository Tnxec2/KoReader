package com.kontranik.koreader.model

class Cursor(
        var page: Int = 0,
        var element: Int = 0,
        var paragraph: Int = 0,
        var symbol: Int = 0
) {
    constructor(c: Cursor): this(
            page = c.page,
            element=c.element,
            paragraph = c.paragraph,
            symbol=c.symbol)
}