package com.kontranik.koreader.model

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.TextView
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.parser.epubreader.EpubHelper
import com.kontranik.koreader.parser.fb2reader.FB2Helper
import com.kontranik.koreader.utils.PageLoader
import org.jsoup.Jsoup
import org.jsoup.nodes.Attributes
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Tag
import kotlin.math.ceil


class Book(private var context: Context, var fileLocation: String, pageView: TextView) {

    var curPage: Page? = Page(null, BookPosition())

    var scheme: BookScheme = BookScheme()

    private var ebookHelper: EbookHelper? = null

    private var pageLoader: PageLoader = PageLoader(pageView, this)

    init {
        loadBook()
    }

    private fun loadBook() {
        if (fileLocation.endsWith(".epub", true)) {
            ebookHelper = EpubHelper(fileLocation)
            ebookHelper!!.readBook()
            calculateScheme()
        } else if (
            fileLocation.endsWith(".fb2", ignoreCase = true)
            || fileLocation.endsWith(".fb2.zip", ignoreCase = true)
                ) {
            ebookHelper = FB2Helper(context, fileLocation)
            ebookHelper!!.readBook()
            calculateScheme()
        }
    }

    private fun calculateScheme() {
        if ( ebookHelper == null || ebookHelper!!.getContentSize() == 0   ) return

        scheme = BookScheme()
        scheme.sectionCount = ebookHelper!!.getContentSize()
        for( pageIndex in 0 until scheme.sectionCount) {
            val textSize = getPageTextSize(pageIndex)
            val pages = ceil(textSize.toDouble() / BookScheme.CHAR_PER_PAGE).toInt()
            scheme.scheme[pageIndex] = BookSchemeCount(textSize = textSize, textPages = pages)
            scheme.textSize += textSize
            scheme.textPages += pages
        }
    }

    private fun getPageTextSize(page: Int): Int {
        val aSection = ebookHelper?.getPage(page)
        val document = Jsoup.parse(aSection)
        return document.body().wholeText().length
    }

    fun getPageBody(page: Int): String? {
        val aSection = ebookHelper?.getPage(page)
        val document = Jsoup.parse(aSection)

        removeHead(document)
        replaceSvgWithImg(document)

        return document.html()
    }

    private fun removeHead(document: Document) {
        val head: Element = document.head()
        head.select("title").remove()
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
                val img = Element(Tag.valueOf("img"), null, attrs)
                svg.replaceWith(img)
            }
        }
    }

    fun getImageByteArray(source: String): ByteArray? {
        if ( ebookHelper != null) {
            val resource = ebookHelper!!.getImageByHref(source)
            if (resource != null) {
                return resource
            }
        }
        return  null
    }

    fun getImageBitmapDrawable(source: String): BitmapDrawable? {
        if ( ebookHelper != null) {
            val resource = ebookHelper!!.getImageByHref(source)
            if (resource != null) {
                val bitmap = BitmapFactory.decodeByteArray(resource, 0, resource.size)
                return BitmapDrawable(context.resources, bitmap)
            }
        }
        return  null
    }

    fun getNote(href: String): String? {
        if ( ebookHelper != null) {
            val resource = ebookHelper!!.getPageByHref(href)
            if (resource != null) {
                val document = Jsoup.parse(resource)
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