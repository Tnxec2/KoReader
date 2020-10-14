package com.kontranik.koreader.model

class Cursor(
        var bookPage: Int = 0,
        var pageElement: Int = 0,
        var paragraph: Int = 0,
        var word: Int = 0,
        var symbol: Int = 0
) {
    constructor(c: Cursor): this(
            bookPage = c.bookPage,
            pageElement=c.pageElement,
            paragraph = c.paragraph,
            word=c.word,
            symbol=c.symbol)
}