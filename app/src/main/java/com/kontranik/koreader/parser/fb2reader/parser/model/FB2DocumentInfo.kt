package com.kontranik.koreader.parser.fb2reader.parser.model

import java.util.*

class FB2DocumentInfo {
    var authors: MutableList<Author> = mutableListOf()
    var version: String? = null
    var history = StringBuffer()
    var date: Date? = null

    fun setHistory(history: String?) {
        this.history = StringBuffer(history!!)
    }
}