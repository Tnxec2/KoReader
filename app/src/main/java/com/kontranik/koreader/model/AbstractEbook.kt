package com.kontranik.koreader.model

class AbstractEbook {

    var cover: ByteArray? = null
    var title: String? = null
    var authors: List<Author> = mutableListOf()
    var pages: MutableList<String> = mutableListOf()


    var bookPath: String? = null
}