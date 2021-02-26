package com.kontranik.koreader.model

class BookPageScheme {
    var sectionCount: Int = 0
    var textSize: Int = 0
    var textPages: Int = 0
    var sections: MutableList<String> = mutableListOf()
    var scheme: HashMap<Int, BookSchemeItem> = HashMap()

    companion object {
        const val CHAR_PER_PAGE = 1500
        const val MAX_PAGE_PER_SECTION = 50
    }

    fun getBookPositionForPage(page: Int): BookPosition {
        var fullSectionPages = 0
        var section = 0
        var sectionPages = 0;
        var offset = 0;
        for (i in 0 until sectionCount-1) {
            section = i
            sectionPages = scheme[i]!!.textPages
            if ( page <= fullSectionPages + sectionPages ) break
            fullSectionPages += sectionPages
        }
        offset = ( page - fullSectionPages) * CHAR_PER_PAGE

        return BookPosition(section, offset)
    }
}

class BookSchemeItem(var textSize: Int = 0,
                     var textPages: Int = 0) {
}