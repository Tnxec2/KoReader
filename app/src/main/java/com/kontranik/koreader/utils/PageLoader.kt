package com.kontranik.koreader.utils

import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.kontranik.koreader.model.*

class PageLoader @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)

    constructor(private val pageView: TextView, private val book: Book) : PageSplitterHtml(pageView){

    fun getPage(bookPosition: BookPosition, revers: Boolean, recalc: Boolean): Page? {
        Log.d(TAG, "getPage:  $bookPosition , revers = $revers, recalc = $recalc")
        var result: Page?
        if ( pages.isEmpty() || pages[0].startBookPosition.section != bookPosition.section || recalc) {
            loadPages(bookPosition.section)
        }

         result = findPage(bookPosition.offSet)

        var section = bookPosition.section
        while ( result == null) {
            if ( ! revers) section++
            else section--
            if ( section < 0 || section > book.scheme.sectionCount) break
            loadPages(section)
            result = if ( pages.isEmpty() ) null else { if ( ! revers) pages[0] else pages[pages.size-1] }
        }

        return result
    }

    private fun findPage(startOffset: Int): Page? {
        if ( startOffset < 0) return null
        if ( pages.isEmpty() ) return null
        if ( startOffset > pages[pages.size-1].endBookPosition.offSet) return null

        var start: Int
        var end: Int
        for (  i in 0 until pages.size) {

            //start = if (i == 0) 0 else pages[i - 1].endBookPosition.offSet + 1
            start = pages[i].startBookPosition.offSet
            end = pages[i].endBookPosition.offSet

            if ( startOffset in start until  end) {
                return pages[i]
            }
        }
        return null
    }

    private fun loadPages(section: Int) {
        if (section > 0 && section < book.scheme.sectionCount) {
            val html = book.getPageBody(section)
            if ( html != null) {
                splitPages(book, section, html)
            }
        } else if ( section == 0 ) {
            val pageWidth: Int = pageView.measuredWidth - pageView.paddingLeft - pageView.paddingRight
            val pageHeight: Int = pageView.measuredHeight - pageView.paddingTop - pageView.paddingBottom
            pages = mutableListOf(coverPage(pageWidth, pageHeight))
        }
    }

    private fun coverPage(width: Int, height: Int): Page {
        val content = book.getCover(width, height)
        val startCursor = BookPosition(0, 0)
        val endCursor = BookPosition(1, 0)
        return Page( content, startCursor, endCursor)
    }

    companion object {
        const val TAG = "PageLoader"
    }
}