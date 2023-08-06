package com.kontranik.koreader.parser.fb2reader

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookPageScheme
import com.kontranik.koreader.model.BookSchemeItem
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.parser.fb2reader.parser.model.FB2Scheme
import com.kontranik.koreader.parser.fb2reader.parser.FB2Reader
import com.kontranik.koreader.parser.fb2reader.parser.model.FB2Elements
import com.kontranik.koreader.parser.fb2reader.parser.model.FB2Section
import com.kontranik.koreader.utils.ImageUtils
import org.jsoup.Jsoup
import java.io.FileNotFoundException
import kotlin.math.ceil

class FB2Helper(private val context: Context, private val contentUri: String) : EbookHelper {

    private var fb2Reader: FB2Reader = FB2Reader(
            if (context.externalCacheDir != null) context.externalCacheDir!!.absolutePath
            else context.filesDir.absolutePath, contentUri)

    override var bookInfo: BookInfo? = null
    override var pageScheme: BookPageScheme = BookPageScheme()

    override fun readBook() {
        try {
            val fileInputStream =
                context.contentResolver.openInputStream(Uri.parse(this.contentUri)) ?: return
            fb2Reader.readBook(contentUri, fileInputStream)
            if (fb2Reader.fb2Scheme != null) {
                bookInfo = BookInfo(
                    title = fb2Reader.fb2Scheme!!.description.titleInfo.booktitle,
                    cover = getcoverbitmap(),
                    authors = getAuthors(fb2Reader.fb2Scheme!!).toMutableList(),
                    path = contentUri,
                    filename = contentUri,
                    annotation = fb2Reader.fb2Scheme!!.description.titleInfo.annotation.toString()
                )
            }
            calculateScheme()
        }  catch (_: FileNotFoundException) {

        }
    }

    private fun calculateScheme() {
        if ( fb2Reader.fb2Scheme == null ) return
        pageScheme = BookPageScheme()
        pageScheme.sectionCount = getContentSize()
        if ( pageScheme.sectionCount == 0) return
        for( pageIndex in 0 .. pageScheme.sectionCount) {
            if (pageIndex > 0 && ( fb2Reader.fb2Scheme!!.sections[pageIndex-1].isNote
                        || fb2Reader.fb2Scheme!!.sections[pageIndex-1].isComment)) continue
            pageScheme.sectionCountWithOutNotes += 1
            val textSize = getPageTextSize(pageIndex)
            if (textSize != null) {
                val pages = ceil(textSize.toDouble() / BookPageScheme.CHAR_PER_PAGE).toInt()
                pageScheme.scheme[pageIndex] = BookSchemeItem(
                    textSize = textSize, countTextPages = pages
                )
                pageScheme.textSize += textSize
                pageScheme.countTextPages += pages
            }
        }
        pageScheme.sections = mutableListOf()
        pageScheme.sections.add("(0) Cover")
        fb2Reader.fb2Scheme!!.sections.forEachIndexed { index, it ->
            val title = it.title ?: it.orderid.toString()
            pageScheme.sections.add("(${index + 1})${getNotes(it)} $title")
        }
    }

    private fun getNotes(it: FB2Section): String {
        return if (it.typ != FB2Elements.BODY && it.isNote)  "\uD83D\uDCDD"  // 📝
            else if (it.typ != FB2Elements.BODY && it.isComment)  "\uD83D\uDDE8️" else "" // 🗨️
    }

    private fun getPageTextSize(pageIndex: Int): Int? {
        if ( pageIndex != 0 ) {
            val textsize = fb2Reader.fb2Scheme!!.sections[pageIndex-1].textsize
            if ( textsize != null ) return textsize
        }
        val aSection = getPage(pageIndex)
        return if ( aSection != null) {
            getSizeOfHtmlText(aSection)
        } else {
            null
        }
    }

    override fun getContentSize(): Int {
        return if (fb2Reader.fb2Scheme == null) 0 else fb2Reader.fb2Scheme!!.sections.size
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
        return if (tempScheme != null) {
            val t = tempScheme.description.titleInfo.booktitle
            val coverImage = tempScheme.cover
            val coverBitmap: Bitmap? = getCoverbitmap(coverImage?.contentsArray)
            BookInfo(
                title = t,
                cover = coverBitmap,
                authors = getAuthors(tempScheme).toMutableList(),
                path = contentUri,
                filename = contentUri,
                annotation = tempScheme.description.titleInfo.annotation.toString()
            )
        } else {
            null
        }
    }

    private fun getcoverbitmap(): Bitmap? {
        return getCoverbitmap(fb2Reader.fb2Scheme?.cover?.contentsArray)
    }

    private fun getCoverbitmap(coverImage: ByteArray?): Bitmap? {
        var coverBitmap: Bitmap? = null
        if ( coverImage != null)  coverBitmap = ImageUtils.byteArrayToBitmap(coverImage)
        return coverBitmap
    }

    private fun getAuthors(tempScheme: FB2Scheme): List<Author> {
        return tempScheme.description.titleInfo.authors.map {
            Author(null, it.firstname?.trim(), it.middlename?.trim(), it.lastname?.trim())
        }
    }

    override fun getCoverPage(): String? {
        return if (fb2Reader.fb2Scheme == null) null else fb2Reader.fb2Scheme!!.description.titleInfo.coverpage.toString()
    }

    companion object {
        fun getSizeOfHtmlText(htmlText: String): Int {
            val document = Jsoup.parse(htmlText)
            return document.body().wholeText().length
        }
    }
}