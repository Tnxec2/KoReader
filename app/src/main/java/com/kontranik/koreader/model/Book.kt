package com.kontranik.koreader.model

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.TextView
import com.kontranik.koreader.reader.PageLoader
import com.kontranik.koreader.utils.EpubHelper
import nl.siegmann.epublib.domain.Book
import org.jsoup.Jsoup
import kotlin.math.ceil

class Book(private var c: Context, private var fileLocation: String) {

    var curPage: Page? = Page(null, BookPosition())
    var nextPage: Page? = null
    var prevPage: Page? = null

    var scheme: BookScheme = BookScheme()

    private var epubHelper: EpubHelper? = null
    private var epubBook: Book? = null

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
            val pages = ceil( textSize.toDouble() / BookScheme.CHAR_PER_PAGE).toInt()
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
        document.select("head").remove()
        return document.html()
    }

    private fun loadPage(page: Page, textView: TextView): Page? {
        return PageLoader(textView, this).loadPage(page)
    }

    private fun loadPageRevers(page: Page, textView: TextView): Page? {
        return PageLoader(textView, this).loadPageRevers(page)
    }

    fun loadPage(pageView: TextView) {
        loadCurPage(pageView)
        loadPrevPage(pageView)
        if ( prevPage != null && prevPage!!.recalculate && curPage != null ) {
            curPage!!.startBookPosition = BookPosition(prevPage!!.endBookPosition)
            loadCurPage(pageView)
        }
        loadNextPage(pageView)
    }

    private fun loadCurPage(pageView: TextView) {
        Log.d("Book", "loadCurPage...")
        curPage = loadPage(Page(null, curPage!!.startBookPosition), pageView)
    }

    fun loadNextPage(pageView: TextView) {
        Log.d("Book", "loadNextPage...")
        nextPage = loadPage(Page(null, BookPosition(curPage!!.endBookPosition)), pageView)
    }

    fun loadPrevPage(pageView: TextView){
        Log.d("Book", "loadPrevPage...")
        prevPage = loadPageRevers(Page(null, BookPosition(), BookPosition(curPage!!.startBookPosition)), pageView)
    }


}