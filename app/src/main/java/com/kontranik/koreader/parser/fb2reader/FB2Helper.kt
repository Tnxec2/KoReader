package com.kontranik.koreader.parser.fb2reader

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.kontranik.koreader.model.Author
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookPageScheme
import com.kontranik.koreader.model.BookSchemeItem
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.parser.fb2reader.model.FB2Scheme
import com.kontranik.koreader.utils.ImageUtils
import org.jsoup.Jsoup
import kotlin.math.ceil

class FB2Helper(private val context: Context, private val contentUri: String) : EbookHelper {

    private var fb2Reader: FB2Reader = FB2Reader(
            if (context.externalCacheDir != null) context.externalCacheDir!!.absolutePath
            else context.filesDir.absolutePath, contentUri)

    override var bookInfo: BookInfo? = null
    override var pageScheme: BookPageScheme = BookPageScheme()

    override fun readBook() {
        val fileInputStream = context.contentResolver.openInputStream(Uri.parse(this.contentUri)) ?: return
        fb2Reader.readBook(contentUri, fileInputStream)
        if ( fb2Reader.fb2Scheme != null) {
            bookInfo = BookInfo(
                    title = fb2Reader.fb2Scheme.description.titleInfo.booktitle,
                    cover = getcoverbitmap(),
                    authors = getAuthors(fb2Reader.fb2Scheme).toMutableList(),
                    path = contentUri,
                    filename = contentUri,
                    annotation = fb2Reader.fb2Scheme.description.titleInfo.annotation.toString()
            )
        }
        calculateScheme()
    }

    private fun calculateScheme() {
        if ( fb2Reader.fb2Scheme != null && getContentSize() == 0  ) return
        pageScheme = BookPageScheme()
        pageScheme.sectionCount = getContentSize() + 1 // coverPage dazu
        for( pageIndex in 0 until pageScheme.sectionCount) {
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
        pageScheme.sections.add("Cover")
        fb2Reader.fb2Scheme.sections.forEach{
            val title = it.title ?: it.orderid.toString()
            pageScheme.sections.add(title)
        }

    }

    private fun getPageTextSize(aSection: String): Int {
        val document = Jsoup.parse(aSection)
        return document.body().wholeText().length
    }

    override fun getContentSize(): Int {
        return fb2Reader.fb2Scheme.sections.size
    }

    override fun getPage(page: Int): String? {
        if ( page == 0) return getCoverPage()
        return fb2Reader.getSectionHtml(page - 1)
    }

    override fun getPageByHref(href: String): String? {
        return fb2Reader.getSectionHtml(href)
    }

    override fun getImageByHref(href: String): ByteArray? {
        return fb2Reader.getBinary(href)
    }

    override fun getBookInfoTemporary(contentUri: String): BookInfo? {
        val tempReader = FB2Reader(context.filesDir.absolutePath, contentUri)
        val fileInputStream = context.contentResolver.openInputStream(Uri.parse(contentUri)) ?: return null
        val tempScheme = tempReader.readScheme(fileInputStream)
        if (tempScheme != null) {
            val t = tempScheme.description.titleInfo.booktitle
            val coverImage = tempScheme.cover
            var coverBitmap: Bitmap?
            coverBitmap = getCoverbitmap(coverImage?.contentsArray)
            return BookInfo(
                    title = t,
                    cover = coverBitmap,
                    authors = getAuthors(tempScheme).toMutableList(),
                    path = contentUri,
                    filename = contentUri,
                    annotation = tempScheme.description.titleInfo.annotation.toString()
            )
        } else {
            return null
        }
    }

    private fun getcoverbitmap(): Bitmap? {
        return getCoverbitmap(fb2Reader?.fb2Scheme?.cover?.contentsArray)
    }

    private fun getCoverbitmap(coverImage: ByteArray?): Bitmap? {
        var coverBitmap: Bitmap? = null
        if ( coverImage != null)  coverBitmap = ImageUtils.byteArrayToBitmap(coverImage)
        return coverBitmap
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