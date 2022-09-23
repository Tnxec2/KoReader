package com.kontranik.koreader.parser.fb2reader.parser

import com.kontranik.koreader.parser.fb2reader.parser.model.FB2Scheme
import java.io.InputStream
import java.util.logging.Level
import java.util.logging.Logger

class FB2Reader internal constructor(private val appDir: String,
                                     /**
                                      * @return the uri
                                      */
                                     val contentUri: String) {

    var fb2Scheme: FB2Scheme? = null
        private set

    fun readBook(contentUri: String?, fileInputStream: InputStream?): FB2Scheme? {
        if (contentUri != null) {
            try {
                val schemeFile = FileHelper(appDir).scheme
                if (contentUri == schemeFile.path) {
                    fb2Scheme = schemeFile
                    return schemeFile
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Logger.getLogger("FB2READER").log(Level.INFO, e.message)
            }
        }
        try {
            fb2Scheme = FB2Parser(appDir, contentUri!!, fileInputStream!!).parseBook()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return fb2Scheme
    }

    fun readScheme(fileInputStream: InputStream?): FB2Scheme? {
        try {
            fb2Scheme = FB2Parser(appDir, contentUri, fileInputStream!!).parseScheme()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return fb2Scheme
    }

    val coverPage: String
        get() = fb2Scheme!!.description.titleInfo.coverpage.toString()

    val cover: ByteArray?
        get() {
            var coverSrc = fb2Scheme!!.description.titleInfo.coverImageSrc
            if (coverSrc != null) {
                if (coverSrc.startsWith("#")) coverSrc = coverSrc.substring(1)
                return try {
                    val binaryData = FileHelper(appDir).getBinary(coverSrc)
                    binaryData.contentsArray
                } catch (e: Exception) {
                    null
                }
            }
            return null
        }

    fun getSectionHtml(orderId: Int): String? {
        return if (orderId < 0 || orderId > fb2Scheme!!.sections.size) null else try {
            FileHelper(appDir).getSectionText(orderId)
        } catch (e: Exception) {
            null
        }
    }

    fun getSectionHtml(sectionName: String): String? {
        var name = sectionName
        if (name.startsWith("#")) name = name.substring(1)
        val s = fb2Scheme!!.getSection(name)
        return if (s != null) {
            getSectionHtml(s.orderid)
        } else {
            null
        }
    }

    fun getBinary(binaryname: String): ByteArray? {
        var name = binaryname
        if (name.startsWith("#")) name = name.substring(1)
        return try {
            val binaryData = FileHelper(appDir).getBinary(name)
            binaryData.contentsArray
        } catch (e: Exception) {
            null
        }
    }

    init {
        Logger.getLogger("FB2READER").log(Level.INFO, appDir)
    }
}