package com.kontranik.koreader.parser.epubreader

import android.graphics.Bitmap
import com.kontranik.koreader.model.*
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.ImageUtils

import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.math.ceil

class EpubHelper(private val fileLocation: String) : EbookHelper {

    private var epubReader = EpubReader()
    private var epubBook: Book? = null
    override var pageScheme: BookPageScheme = BookPageScheme()

    override fun readBook() {
        readBook(fileLocation)
        calculateScheme()
    }

    private fun readBook(path: String): Book? {
        val bookFile = File(path)

        try {
            val fileInputStream = FileInputStream(bookFile)
            epubBook = epubReader.readEpub(fileInputStream)
            fileInputStream.close()
            return epubBook
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun calculateScheme() {
        if ( epubBook != null && getContentSize() == 0   ) return
        pageScheme = BookPageScheme()
        pageScheme.sectionCount = getContentSize()
        for( pageIndex in 0 until getContentSize()) {
            val textSize = getPageTextSize(pageIndex)
            val pages = ceil(textSize.toDouble() / BookPageScheme.CHAR_PER_PAGE).toInt()
            pageScheme.scheme[pageIndex] = BookSchemeCount(
                    textSize = textSize, textPages = pages)
            pageScheme.textSize += textSize
            pageScheme.textPages += pages
        }
    }

    private fun getPageTextSize(page: Int): Int {
        val aSection = getPage(page)
        val document = Jsoup.parse(aSection)
        return document.body().wholeText().length
    }

    override fun getContentSize(): Int {
        return epubBook!!.contents.size
    }

    override fun getPage(page: Int): String? {
        val data = epubBook?.contents?.get(page)?.data
        return data?.let { String(it) }
    }

    override fun getPageByHref(href: String): String? {
        val data = epubBook?.resources?.getByHref(href)?.data
        return data?.let { String(it) }
    }

    override fun getImageByHref(href: String): ByteArray? {
        return epubBook!!.resources.getByHref(href)?.data
    }

    override fun getBookInfoTemporary(path: String): BookInfo? {
        val eb = readBook(path)
        if (eb != null) {
            val t = eb.title
            val coverImage = eb.coverImage
            var coverBitmap: Bitmap? = null
            if (coverImage != null) {
                if ( coverImage.data != null)  coverBitmap = ImageUtils.byteArrayToBitmap(coverImage.data)
            }
            return BookInfo(
                    title =  t,
                    cover = coverBitmap,
                    authors = getAuthors(eb),
                    path = path,
                    filename = File(path).name,
                    annotation = eb.metadata.descriptions.joinToString(separator = "\n", prefix = "<p>", postfix = "</p>")
            )
        } else {
            return null
        }
    }

    private fun getAuthors(eBook: Book): List<Author> {
        return eBook.metadata.authors.map {
            Author(it.firstname, middlename = null, it.lastname)
        }
    }

    override fun getCoverPage(): String? {
        val data = epubBook?.coverPage?.data
        return data?.let { String(it) }
    }

}