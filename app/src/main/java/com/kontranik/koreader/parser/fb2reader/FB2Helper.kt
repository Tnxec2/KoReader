package com.kontranik.koreader.parser.fb2reader

import android.content.Context
import android.graphics.Bitmap
import com.kontranik.koreader.model.*
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.parser.fb2reader.model.FB2Scheme
import com.kontranik.koreader.utils.ImageUtils
import org.jsoup.Jsoup

import java.io.File
import kotlin.math.ceil

class FB2Helper(private val context: Context , fileLocation: String) : EbookHelper {

    private var fb2Reader: FB2Reader = FB2Reader(
       if ( context.externalCacheDir != null) context.externalCacheDir!!.absolutePath
       else context.filesDir.absolutePath, fileLocation)
    override var pageScheme: BookPageScheme = BookPageScheme()

    override fun readBook() {
        fb2Reader.readBook()
        calculateScheme()
    }

    private fun calculateScheme() {
        if ( fb2Reader.fb2Scheme != null && getContentSize() == 0   ) return
        pageScheme = BookPageScheme()
        pageScheme.sectionCount = getContentSize() + 1 // coverPage dazu
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
        return fb2Reader.fb2Scheme.sections.size
    }

    override fun getPage(page: Int): String? {
        if ( page == 0) return getCoverPage()
        return fb2Reader.getSectionHtml(page-1)
    }

    override fun getPageByHref(href: String): String? {
        return fb2Reader.getSectionHtml(href)
    }

    override fun getImageByHref(href: String): ByteArray? {
        return fb2Reader.getBinary(href)
    }

    override fun getBookInfoTemporary(path: String): BookInfo? {
        val tempReader = FB2Reader(context.filesDir.absolutePath, path)
        val tempScheme = tempReader.readScheme()
        if (tempScheme != null) {
            val t = tempScheme.description.titleInfo.booktitle
            val coverImage = tempScheme.cover
            var coverBitmap: Bitmap? = null
            if (coverImage != null) {
                if ( coverImage.contentsArray != null)
                    coverBitmap = ImageUtils.byteArrayToBitmap(coverImage.contentsArray)
            }
            return BookInfo(
                    title =  t,
                    cover = coverBitmap,
                    authors = getAuthors(tempScheme),
                    path = path,
                    filename = File(path).name,
                    annotation = tempScheme.description.titleInfo.annotation.toString()
            )
        } else {
            return null
        }
    }

    private fun getAuthors(tempScheme: FB2Scheme): List<Author> {
        return tempScheme.description.titleInfo.authors.map {
            Author(it.firstname, it.middlename, it.lastname)
        }
    }

    override fun getCoverPage(): String? {
        return fb2Reader.fb2Scheme.description.titleInfo.coverpage.toString()
    }

}