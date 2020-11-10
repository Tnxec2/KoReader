package com.kontranik.koreader.reader

import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.kontranik.koreader.model.*
import org.jsoup.nodes.Element
import kotlin.math.max
import kotlin.math.min

class PageLoader @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)

    constructor(pageView: TextView, val book: Book) : PageSplitterOne(
        pageView.measuredWidth - pageView.paddingLeft - pageView.paddingRight,
        pageView.measuredHeight - pageView.paddingTop - pageView.paddingBottom,
        pageView.paint, pageView.lineSpacingMultiplier, pageView.lineSpacingExtra, pageView.context) {

    fun loadPage(page: Page): Page? {
        val result: Page?

        clear()

        Log.d(TAG, "loadPage...")

        if ( page.startBookPosition.page >= book.schema.pageCount ) return  null

        var bookPage = page.startBookPosition.page

        var lastElement = 0

        while (bookPage > 0 && bookPage < book.schema.pageCount) {
            Log.d(TAG, "loadPage: bookPage = " + bookPage)
            val elements = book.getElements(bookPage)
            lastElement = if ( bookPage == page.startBookPosition.page) page.startBookPosition.element else 0

            while (lastElement < elements!!.size) {
                val element = elements[lastElement]

                val parents = element.parents()

                val startParagraph = if ( bookPage == page.startBookPosition.page) page.startBookPosition.paragraph else 0
                val startSymbol = if ( bookPage == page.startBookPosition.page && lastElement == page.startBookPosition.element) page.startBookPosition.symbol else 0

                Log.d(TAG, "loadPage: element = " + lastElement + ", par: " + startParagraph + ", startSymbol: " + startSymbol)
                append(getLine(element), startParagraph, startSymbol, false)
                if ( pageOne != null) break
                lastElement++
            }

            if ( pageOne != null) break
            bookPage++
        }

        if ( pageOne == null && bookPage >= book.schema.pageCount ) {
            getLastPage()
        }

        if ( bookPage == 0 ) {
            result = coverPage(pageWidth, pageHeight)
        } else {
            bookPage = min(bookPage, book.schema.pageCount)
            val pageElement = lastElement
            val paragraph = paragraphIndex
            val symbol = symbolIndex
            val content = this.pageOne
            val endCursor = BookPosition(bookPage, pageElement, paragraph, symbol)
            result = Page(content, BookPosition(page.startBookPosition), endCursor )
        }
        return result
    }

    fun loadPageRevers(page: Page): Page? {
        val result: Page?

        Log.d(TAG, "loadPageRevers...")

        if ( page.endBookPosition.page == 0 && page.endBookPosition.element == 0 && page.endBookPosition.paragraph == 0 && page.endBookPosition.symbol == 0) return  null

        clear()

        var bookPage = page.endBookPosition.page
        var lastElement = 0

        while (bookPage > 0) {
            val elements = book.getElements(bookPage)
            lastElement = if (bookPage == page.endBookPosition.page) page.endBookPosition.element else elements.size-1
            while (lastElement > 0) {
                val element = elements.get(lastElement)
                val startParagraph = if (bookPage == page.endBookPosition.page) page.endBookPosition.paragraph else null
                val startSymbol = if (bookPage == page.endBookPosition.page && lastElement == page.endBookPosition.element) max(page.endBookPosition.symbol - 1, 0)  else null

                append(getLine(element), startParagraph, startSymbol, true)
                if (status == PageSplitterStatus.PageFull) break
                lastElement--
            }
            if (status == PageSplitterStatus.PageFull) break
            bookPage--
        }

        if ( bookPage == 0 && status == PageSplitterStatus.PageStartet ) {
            val newpage = Page( null, BookPosition(1), BookPosition() )
            result = loadPage(newpage)
            if ( result != null) result.recalculate = true
            return result
        }

        if ( bookPage == 0 ) {
            result = coverPage(pageWidth, pageHeight)
        } else {
            bookPage = max(bookPage, 0)
            val pageElement = max(lastElement, 0)
            val paragraph = max(paragraphIndex, 0)
            val symbol = max(symbolIndex, 0)
            val content = pageOne
            val startCursor = BookPosition(bookPage, pageElement, paragraph, symbol)
            result = Page(content, startCursor, BookPosition(page.endBookPosition) )

        }
        return result
    }

    private fun getLine(element: Element): Line {
        val tag = element.normalName()
        // val myStyle = MyStyle.getFromString(tag)
        val myStyle = MyStyle.parseJsoupElement(element)
        val text = if (myStyle == MyStyle.Other) {
            "$tag::" + element.ownText()
        } else {
            element.ownText()
        }
        return Line(text, myStyle)
    }

    private fun coverPage(width: Int, height: Int): Page {
        val content = book.getCover(width, height)
        val startCursor = BookPosition(0, 0, 0, 0)
        val endCursor = BookPosition(1, 0, 0, 0)
        return Page( content, startCursor, endCursor)
    }
}