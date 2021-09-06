package com.kontranik.koreader.parser.fb2reader.model

import java.util.*

class FB2TitleInfo {

    var genre: MutableList<String> = mutableListOf()
    var authors: MutableList<Author> = mutableListOf()
    var booktitle: String? = null
    var annotation = StringBuffer()
    var keywords: MutableList<String> = mutableListOf()
    var date: String? = null
    var coverpage = StringBuffer()
    var coverImageSrc: String? = null
    var lang: String? = null
    var srclang: String? = null
    var translators: MutableList<Author> = mutableListOf()
    var sequenceName: String? = null
    var sequenceNumber: String? = null
}