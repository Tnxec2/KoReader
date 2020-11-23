package com.kontranik.koreader.model

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.text.SpannableStringBuilder
import android.widget.TextView
import com.kontranik.koreader.utils.PageLoader
import com.kontranik.koreader.utils.EpubHelper
import nl.siegmann.epublib.domain.Book
import org.jsoup.Jsoup
import org.jsoup.nodes.Attributes
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Tag
import kotlin.math.ceil


class Book(private var c: Context, var fileLocation: String, pageView: TextView) {

    var curPage: Page? = Page(null, BookPosition())

    var scheme: BookScheme = BookScheme()

    private var epubHelper: EpubHelper? = null
    private var epubBook: Book? = null

    private var pageLoader: PageLoader = PageLoader(pageView, this)

    init {
        loadBook()
    }

    private fun loadBook() {
        if (fileLocation.endsWith("epub")) {
            epubHelper = EpubHelper(c, fileLocation)
            epubBook = epubHelper!!.epubBook
            calculateScheme()
        }
    }

    private fun calculateScheme() {
        if ( epubBook == null || epubBook!!.contents.size == 0   ) return

        scheme = BookScheme()
        scheme.sectionCount = epubBook!!.contents.size
        for( pageIndex in 0 until scheme.sectionCount) {
            val textSize = getPageTextSize(pageIndex)
            val pages = ceil(textSize.toDouble() / BookScheme.CHAR_PER_PAGE).toInt()
            scheme.scheme[pageIndex] = BookSchemeCount(textSize = textSize, textPages = pages)
            scheme.textSize += textSize
            scheme.textPages += pages
        }
    }

    fun getCover(width: Int, height: Int): SpannableStringBuilder? {
        return epubHelper?.epubBook?.let { epubHelper!!.getCover(width, height) }
    }

    private fun getPageTextSize(page: Int): Int {
        val aSection = epubHelper?.getPage(page)
        val document = Jsoup.parse(aSection)
        return document.body().wholeText().length
    }

    fun getPageBody(page: Int): String? {
        val aSection = epubHelper?.getPage(page)
        val document = Jsoup.parse(aSection)

        removeHead(document)
        replaceSvgWithImg(document)

        return document.html()
    }

    private fun removeHead(document: Document) {
        document.select("head").remove()
    }

    /*
     * replace svg-image-Tag with img-Tag
     */
    private fun replaceSvgWithImg(document: Document) {
        val svgElements = document.select("svg")
        for ( svg in svgElements) {
            val imagesElements = svg.select("image")
            if ( imagesElements.isNotEmpty()) {
                val svgImage = imagesElements[0]
                val attrs = Attributes()
                attrs.add("src", svgImage.attr("xlink:href"))
                val img = Element(Tag.valueOf("img"), null, attrs  )
                svg.replaceWith(img)
            }
        }
    }

    fun getImageByteArray(source: String): ByteArray? {
        if ( epubBook != null) {
            val resource = epubBook!!.resources.getByHref(source)
            if (resource != null) {
                return resource.data
            }
        }
        return  null
    }

    fun getImageBitmapDrawable(source: String): BitmapDrawable? {
        if ( epubBook != null) {
            val resource = epubBook!!.resources.getByHref(source)
            if (resource != null) {
                val mImage =  resource.data
                val bitmap = BitmapFactory.decodeByteArray(mImage, 0, mImage.size)
                return BitmapDrawable(c.resources, bitmap)
            }
        }
        return  null
    }

    fun getNote(href: String): String? {
        if ( epubBook != null) {
            val resource = epubBook!!.resources.getByHref(href)
            if (resource != null) {
                val s = String(resource.data)
                val document = Jsoup.parse(s)
                removeHead(document)
                return document.html()
            }
        }
        return null
    }

    fun getCur(recalc: Boolean): Page? {
        return pageLoader.getPage(BookPosition(curPage!!.startBookPosition), false, recalc)
    }

    fun getNext(): Page? {
        val bookPosition =  BookPosition(curPage!!.endBookPosition)
        bookPosition.offSet = bookPosition.offSet + 1
        return pageLoader.getPage(BookPosition(bookPosition), revers = false, recalc = false)
    }

    fun getPrev(): Page? {
        val bookPosition =  BookPosition(curPage!!.startBookPosition)
        bookPosition.offSet = bookPosition.offSet - 1
        return pageLoader.getPage(BookPosition(bookPosition), revers = true, recalc = false)
    }
}