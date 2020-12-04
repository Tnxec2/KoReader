package com.kontranik.koreader.model

class BookPageScheme {
    var sectionCount: Int = 0
    var textSize: Int = 0
    var textPages: Int = 0
    var scheme: HashMap<Int, BookSchemeCount> = HashMap()

    companion object {
        const val CHAR_PER_PAGE = 1500
    }
}

class BookSchemeCount(var textSize: Int = 0,
                      var textPages: Int = 0) {
}