package com.kontranik.koreader.parser.epubreader

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookPageScheme
import com.kontranik.koreader.model.BookSchemeItem
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.ImageUtils
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.domain.TOCReference
import nl.siegmann.epublib.epub.EpubReader
import nl.siegmann.epublib.service.MediatypeService
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
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
        if ( getContentSize() == 0   ) return
        pageScheme = BookPageScheme()
        pageScheme.sectionCount = getContentSize()-1
        pageScheme.sectionCountWithOutNotes = pageScheme.sectionCount
        for( pageIndex in 0 .. getContentSize()) {
            val aSection = getPage(pageIndex)

            if ( aSection != null) {
                val textSize = getPageTextSize(aSection)
                val pages = ceil(textSize.toDouble() / BookPageScheme.CHAR_PER_PAGE).toInt()
                pageScheme.scheme[pageIndex] = BookSchemeItem(
                        textSize = textSize, countTextPages = pages)
                pageScheme.textSize += textSize
                pageScheme.countTextPages += pages
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
        if ( epubBook?.contents != null ) {
            if ( page > epubBook?.contents!!.lastIndex) return null
            val data = epubBook?.contents?.get(page)?.data
            return data?.let { String(it) }
        } else
            return null
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
        return if (eb != null) {
            getBookInfoFromBook(eb)
        } else {
            null
        }
    }

    private fun getBookInfoFromBook(eb: Book): BookInfo {
        val t = eb.title

        return BookInfo(
                title = t,
                cover = getCoverBitmap(),
                authors = getAuthors(eb).toMutableList(),
                path = contentUri,
                filename = contentUri,
                annotation = eb.metadata.descriptions.joinToString(separator = "\n", prefix = "<p>", postfix = "</p>"),
        )
    }

    private fun getCoverBitmap(): Bitmap? {

        val coverImage = epubBook?.coverImage
        val coverPage = epubBook?.coverPage
        var coverBitmap: Bitmap? = null
        if ( coverImage != null) {
            coverBitmap = getCoverImage(coverImage)
        } else  if ( coverPage != null) {
            coverBitmap = getCoverImage(coverPage)
        }
        return coverBitmap
    }

    private fun getCoverImage(resource: Resource): Bitmap? {
        var coverBitmap: Bitmap? = null
        if (MediatypeService.isBitmapImage(resource.mediaType)) {
            coverBitmap = ImageUtils.byteArrayToBitmap(resource.data)
        } else if (resource.mediaType == MediatypeService.XHTML) {
            val resourceData = resource.data

            val document = Jsoup.parse(String(resourceData))
            if (document != null) {
                var imageElement: Element? = document.selectFirst("img")

                if (imageElement != null) {
                    val href: String = imageElement.attr("src")
                    val image = getImageByHref(href)
                    coverBitmap = image?.let { ImageUtils.byteArrayToBitmap(it) }
                } else {
                    imageElement = document.selectFirst("image")
                    if (imageElement != null) {
                        val href: String = imageElement.attr("xlink:href")
                        var image = getImageByHref(href)
                        if (image == null) image = getImageByHref("OEBPS/$href")
                        coverBitmap = image?.let { ImageUtils.byteArrayToBitmap(it) }
                    }
                }
            }
        }
        return coverBitmap
    }

    private fun getAuthors(eBook: Book): List<Author> {
        return eBook.metadata.authors.map {
            Author(null, it.firstname, middlename = null, it.lastname)
        }
    }

    override fun getCoverPage(): String? {
        val data = epubBook?.coverPage?.data
        return data?.let { String(it) }
    }

}