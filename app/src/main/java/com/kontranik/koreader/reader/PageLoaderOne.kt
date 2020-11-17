package com.kontranik.koreader.reader

import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.kontranik.koreader.model.*
import kotlin.math.max
import kotlin.math.min

class PageLoaderOne @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)

    constructor(pageView: TextView, val book: Book) : PageSplitterOneHtml(
        pageView.measuredWidth - pageView.paddingLeft - pageView.paddingRight,
        pageView.measuredHeight - pageView.paddingTop - pageView.paddingBottom,
        pageView.paint, pageView.lineSpacingMultiplier, pageView.lineSpacingExtra) {

    fun loadPage(page: Page): Page? {
        val result: Page?

        clear()

        Log.d(TAG, "loadPage...")

        if ( page.startBookPosition.section >= book.scheme.sectionCount ) return  null

        var bookPage = page.startBookPosition.section

        Log.d(TAG, "loadPage: bookPage = " + bookPage)
        while (bookPage > 0 && bookPage < book.scheme.sectionCount) {
            val html = book.getPageBody(bookPage)
            if ( html != null) {
                val offset = if (bookPage == page.startBookPosition.section) page.startBookPosition.offSet else 0
                pageForHtml(html, offset, false)
            }

            if ( pageOne != null) break
            bookPage++
        }

        if ( bookPage == 0 ) {
            result = coverPage(pageWidth, pageHeight)
        } else {
            bookPage = min(bookPage, book.scheme.sectionCount)
            val endCursor = BookPosition(bookPage, offSet = endOffset)
            result = Page(pageOne, BookPosition(page.startBookPosition), endCursor )
        }
        return result
    }

    fun loadPageRevers(page: Page): Page? {
        val result: Page?

        Log.d(TAG, "loadPageRevers...")

        if ( page.endBookPosition.section == 0 && page.endBookPosition.offSet == 0 ) return  null

        clear()

        var bookPage = page.endBookPosition.section
        if ( page.endBookPosition.offSet == 0 && bookPage > 0) {
            bookPage--;
        }

        while (bookPage > 0) {
            val html = book.getPageBody(bookPage)
            if ( html != null) {
                val offset = if (bookPage == page.endBookPosition.section) page.endBookPosition.offSet-1 else null
                pageForHtml(html, offset, true)
            }

            if ( pageOne != null) break
            bookPage--
        }

        if ( bookPage == 0 ) {
            result = coverPage(pageWidth, pageHeight)
        } else {
            bookPage = max(bookPage, 0)
            val startCursor = BookPosition(bookPage, offSet = startOffset)
            result = Page(pageOne, startCursor, BookPosition(page.endBookPosition) )
        }
        return result
    }

    private fun coverPage(width: Int, height: Int): Page {
        val content = book.getCover(width, height)
        val startCursor = BookPosition(0, 0)
        val endCursor = BookPosition(1, 0)
        return Page( content, startCursor, endCursor)
    }
}