package com.kontranik.koreader.model

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.ImageUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Attributes
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Tag


class Book(var fileLocation: String) {

    var curPage: Page = Page(null, BookPosition())
    internal var ebookHelper: EbookHelper? = null

    init {
        ebookHelper = EbookHelper.getHelper(fileLocation)
        println("Book. ebookHelper = ${ebookHelper.toString()}")
        ebookHelper?.readBook()

    }



    fun getPageBody(page: Int): String? {
        val aSection = ebookHelper?.getPage(page) ?: return null
        val document = Jsoup.parse(aSection)

        preProcess(document)
        replaceSvgWithImg(document)
        replacePreWithTT(document)

        return document.html()
    }

    private fun preProcess(document: Document) {
        val head: Element = document.head()
        //head.remove()
        head.select("title").remove()
        head.select("style").remove()
    }

    private fun replacePreWithTT(document: Document) {
        val codeElements = document.select("pre")
        for ( code in codeElements) {
            code.tagName("tt")
        }
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

    fun getImageBitmapDrawable(source: String, colorTint: Int, invertAndTint: Boolean): BitmapDrawable? {
        if ( ebookHelper != null) {
            var s = source
            if ( s.startsWith("../")) {
                s = source.substring(3)
            }
            val resource = ebookHelper!!.getImageByHref(s)
            if (resource != null) {
                val bitmap = BitmapFactory.decodeByteArray(resource, 0, resource.size)
                if (bitmap != null) {
                    return if (invertAndTint) {
                        val inverted = ImageUtils.invertAndTint(bitmap, colorTint)
                        BitmapDrawable(KoReaderApplication.getContext().resources, inverted)
                    } else {
                        BitmapDrawable(KoReaderApplication.getContext().resources, bitmap)
                    }
                }
            }
        }
        return  null
    }

    fun getNote(href: String): String? {
        if ( ebookHelper != null) {
            val resource = ebookHelper!!.getPageByHref(href)
            if (resource != null) {
                val document = Jsoup.parse(resource)
                preProcess(document)
                return document.html()
            }
        }
        return null
    }



    fun getPageScheme(): BookPageScheme? {
        return ebookHelper?.pageScheme
    }

    fun getCurSection(): Int {
        return curPage.endBookPosition.section
    }

    fun getCurTextPage(): Int {
        var curTextPage = 0
        for (i in 0 until curPage.endBookPosition.section) {
            if (getPageScheme()?.scheme?.get(i) != null)
                curTextPage += getPageScheme()!!.scheme[i]!!.countTextPages
        }
        curTextPage += ( curPage.endBookPosition.offSet / BookPageScheme.CHAR_PER_PAGE )
        return curTextPage
    }
}