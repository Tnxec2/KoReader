package com.kontranik.koreader.parser;

import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookPageScheme

interface EbookHelper {

    var bookInfo: BookInfo?
    var pageScheme: BookPageScheme

    fun readBook()
    fun getContentSize(): Int
    fun getPage(page: Int): String?
    fun getPageByHref(href: String): String?
    fun getImageByHref(href: String): ByteArray?
    fun getBookInfoTemporary(contentUri: String): BookInfo?
    fun getCoverPage(): String?
}
