package com.kontranik.koreader.model

class BookSchema {
    var pageCount: Int = 0
    var elementCount: Int = 0
    var paragraphCount: Int = 0
    var symbolCount: Int = 0
    var schema: HashMap<Int, BookSchemaCount> = HashMap()
}

class BookSchemaCount(var element: Int = 0,
                      var paragraph: Int = 0,
                      var symbol: Int = 0) {
}