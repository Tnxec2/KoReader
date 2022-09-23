package com.kontranik.koreader.model

class BookPageScheme {
    var sectionCount: Int = 0
    var sectionCountWithOutNotes: Int = 0
    var textSize: Int = 0
    var countTextPages: Int = 0
    var sections: MutableList<String> = mutableListOf()
    var scheme: HashMap<Int, BookSchemeItem> = HashMap()

    companion object {
        const val CHAR_PER_PAGE = 1500
        const val MAX_PAGE_PER_SECTION = 50
    }

    fun getBookPositionForPage(page: Int): BookPosition {
        var fullSectionPages = 0
        var section = 0
        var sectionPages: Int
        for (i in 0 .. sectionCount) {
            section = i
            sectionPages = scheme[i]!!.countTextPages
            if ( page <= fullSectionPages + sectionPages ) break
            fullSectionPages += sectionPages
        }
        val offset: Int = ( page - fullSectionPages) * CHAR_PER_PAGE

        return BookPosition(section, offset)
    }
}

class BookSchemeItem(var textSize: Int = 0,
                     var countTextPages: Int = 0) {
}