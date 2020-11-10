package com.kontranik.koreader.model

import android.content.Context
import android.os.Build
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.kontranik.koreader.reader.PageLoader
import com.kontranik.koreader.utils.EbookHelper
import com.kontranik.koreader.utils.EpubHelper
import nl.siegmann.epublib.domain.Book
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class Book(private var c: Context, private var fileLocation: String) {

    var curPage: Page? = Page(null, BookPosition())
    var nextPage: Page? = null
    var prevPage: Page? = null

    var schema: BookSchema = BookSchema()

    var epubHelper: EpubHelper? = null
    var epubBook: Book? = null

    init {
        loadBook()
    }

    private fun loadBook() {
        if (fileLocation.endsWith("epub")) {
            epubHelper = EpubHelper(c, fileLocation)
            epubBook = epubHelper!!.epubBook
            calculateSchema()
        }
    }

    private fun calculateSchema() {
        if ( epubBook == null || epubBook!!.contents.size == 0   ) return
        schema = BookSchema()
        schema.pageCount = epubBook!!.contents.size
        for( pageIndex in 0 until schema.pageCount) {
            var elementCount: Int
            var paragraphCount = 0
            var symbolCount = 0
            val elements = getElements(pageIndex)
            elementCount = elements!!.size
            for( elementIndex in 0 until elementCount) {
                val paragraphs = elements[elementIndex].data()?.split("\n")
                if ( paragraphs != null ) {
                    paragraphCount += paragraphs.size
                    for (paragraphIndex in 0 until paragraphs.size) {
                        symbolCount += paragraphs[paragraphIndex].length
                    }
                }
            }
            schema.schema[pageIndex] = BookSchemaCount(elementCount, paragraphCount, symbolCount)
            schema.elementCount += elementCount
            schema.paragraphCount += paragraphCount
            schema.symbolCount += symbolCount
        }
    }

    private fun getEpubPage(page: Int): String? {
        if (page < 0 || page > epubBook!!.contents.size - 1) return null
        var data: String? = null
        if (epubBook != null)  data = epubHelper?.getPage(page)
        return data
    }

    fun getCover(width: Int, height: Int): SpannableStringBuilder? {
        return epubHelper?.epubBook?.let { epubHelper!!.getCover(width, height) }
    }

    fun getElements(page: Int): Elements {
        val aSection = epubHelper?.getPage(page)
        val document = Jsoup.parse(aSection)
        return document.body().select("*")
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun loadPage(page: Page, textView: TextView): Page? {

        return PageLoader(textView, this).loadPage(page)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun loadPageRevers(page: Page, textView: TextView): Page? {
        return PageLoader(textView, this).loadPageRevers(page)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun loadPage(pageView: TextView) {
        loadCurPage(pageView)
        loadPrevPage(pageView)
        if ( prevPage != null && prevPage!!.recalculate && curPage != null ) {
            curPage!!.startBookPosition = BookPosition(prevPage!!.endBookPosition)
            loadCurPage(pageView)
        }
        loadNextPage(pageView)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun loadCurPage(pageView: TextView) {
        Log.d("Book", "loadCurPage...")
        curPage = loadPage(Page(null, curPage!!.startBookPosition), pageView)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun loadNextPage(pageView: TextView) {
        Log.d("Book", "loadNextPage...")
        nextPage = loadPage(Page(null, BookPosition(curPage!!.endBookPosition)), pageView)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun loadPrevPage(pageView: TextView){
        Log.d("Book", "loadPrevPage...")
        prevPage = loadPageRevers(Page(null, BookPosition(), BookPosition(curPage!!.startBookPosition)), pageView)
    }
}