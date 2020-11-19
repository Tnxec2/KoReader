package com.kontranik.koreader.utils

import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.kontranik.koreader.model.*
import kotlin.math.max
import kotlin.math.min

class PageLoader @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)

    constructor(private val pageView: TextView, private val book: Book) : PageSplitterHtml(pageView){

    fun getPage(bookPosition: BookPosition, revers: Boolean, recalc: Boolean): Page? {
        Log.d(TAG, "getPage:  $bookPosition , revers = $revers, recalc = $recalc")
        var result: Page?
        if ( pages.isEmpty() || recalc) {
            loadPages(bookPosition.section)
        } else if ( bookPosition.section < pages[0].startBookPosition.section
                    || bookPosition.section > pages[pages.size-1].endBookPosition.section) {
            loadPages(bookPosition.section)
        }

         result = findPage(bookPosition.offSet)

        var section = bookPosition.section
        while ( result == null) {
            if ( ! revers) section++
            else section--
            section = max(0, section)
            section = min(book.scheme.sectionCount-1, section)
            loadPages(section)
            result = if ( pages.isEmpty() ) null else { if ( ! revers) pages[0] else pages[pages.size-1] }
        }

        return result
    }

    private fun findPage(offset: Int): Page? {
        if ( offset < 0) return null
        if ( pages.isEmpty() ) return null
        if ( offset > pages[pages.size-1].endBookPosition.offSet) return null

        var startOffset: Int
        var endOffset: Int

        for (  i in 0 until pages.size) {
            startOffset = pages[i].startBookPosition.offSet
            endOffset = pages[i].endBookPosition.offSet

            if ( offset in startOffset until  endOffset) {
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
            Log.d(TAG, "cover: $pageWidth x $pageHeight")
            pages = mutableListOf(coverPage(pageWidth, pageHeight))
        }
    }

    private fun coverPage(width: Int, height: Int): Page {
        val content = book.getCover(width, height)
        val startCursor = BookPosition(0, 0)
        val endCursor = BookPosition(0, 1)
        return Page( content, startCursor, endCursor)
    }

    companion object {
        const val TAG = "PageLoader"
    }
}