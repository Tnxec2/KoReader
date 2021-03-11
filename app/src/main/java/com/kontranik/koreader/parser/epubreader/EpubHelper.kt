package com.kontranik.koreader.parser.epubreader

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.kontranik.koreader.model.Author
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookPageScheme
import com.kontranik.koreader.model.BookSchemeItem
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.ImageUtils
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.TOCReference
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.math.ceil

class EpubHelper(private val context: Context, private val contentUri: String) : EbookHelper {

    private var epubReader = EpubReader()
    private var epubBook: Book? = null
    override var bookInfo: BookInfo? = null
    override var pageScheme: BookPageScheme = BookPageScheme()

    override fun readBook() {
        readBook(contentUri)
        calculateScheme()
    }

    private fun readBook(documentUri: String): Book? {
        try {
            val fileInputStream = context.contentResolver.openInputStream(Uri.parse(documentUri)) ?: return null
            epubBook = epubReader.readEpub(fileInputStream)
            if ( epubBook != null) {
                bookInfo = getBookInfoFromBook(epubBook!!)
            }
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
        if ( epubBook == null ) return
        if ( epubBook != null && getContentSize() == 0   ) return
        pageScheme = BookPageScheme()
        pageScheme.sectionCount = getContentSize()
        for( pageIndex in 0 until getContentSize()) {
            val aSection = getPage(pageIndex)
            if ( aSection != null) {
                val textSize = getPageTextSize(aSection)
                val pages = ceil(textSize.toDouble() / BookPageScheme.CHAR_PER_PAGE).toInt()
                pageScheme.scheme[pageIndex] = BookSchemeItem(
                        textSize = textSize, textPages = pages)
                pageScheme.textSize += textSize
                pageScheme.textPages += pages
            }
        }
        pageScheme.sections = mutableListOf()

        epubBook!!.contents.forEachIndexed { index, element ->
            val title = element.title ?: findHrefInTOC(element.href, epubBook!!.tableOfContents.tocReferences)
            pageScheme.sections.add("(${index}) $title")
        }
    }

    private fun findHrefInTOC(href: String, tocr: List<TOCReference>): String? {
        tocr.forEach {
            if (href == it.resource.href) {
                return it.title
            }
            val result = findHrefInTOC(href, it.children)
            if (result != null) return result
        }
        return null
    }

    private fun getPageTextSize(aSection: String): Int {
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

    override fun getBookInfoTemporary(contentUri: String): BookInfo? {
        val eb = readBook(contentUri)
        if (eb != null) {
            return getBookInfoFromBook(eb)
        } else {
            return null
        }
    }

    private fun getBookInfoFromBook(eb: Book): BookInfo {
        val t = eb.title
        val coverImage = eb.coverImage
        var coverBitmap = getCoverbitmap(coverImage?.data)

        return BookInfo(
                title = t,
                cover = coverBitmap,
                authors = getAuthors(eb).toMutableList(),
                path = contentUri,
                filename = contentUri,
                annotation = eb.metadata.descriptions.joinToString(separator = "\n", prefix = "<p>", postfix = "</p>"),
        )
    }

    private fun getcoverbitmap(): Bitmap? {
        return getCoverbitmap(epubBook?.coverImage?.data)
    }

    private fun getCoverbitmap(coverImage: ByteArray?): Bitmap? {
        var coverBitmap: Bitmap? = null
        if ( coverImage != null)  coverBitmap = ImageUtils.byteArrayToBitmap(coverImage)
        return coverBitmap
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

    companion object {
        private const val TAG = "Book"
    }

}