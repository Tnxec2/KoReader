package com.kontranik.koreader.utils

import nl.siegmann.epublib.epub.EpubReader
import java.io.FileInputStream
import java.io.IOException

object EpubHelper {
    var epubReader = EpubReader()
    var fileInputStream: FileInputStream? = null
    @JvmStatic
    fun getCover(path: String?): ByteArray? {
        return try {
            fileInputStream = FileInputStream(path)
            val book = epubReader.readEpub(fileInputStream) ?: return null
            val cover = book.coverImage ?: return null
            cover.data
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}