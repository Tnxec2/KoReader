package com.kontranik.koreader.utils

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.Page
import kotlin.math.max

class PageLoader

    constructor(context: Context, private val book: Book) : PageSplitterHtml(context){

    fun getPage(pageView: TextView, bookPosition: BookPosition, revers: Boolean, recalc: Boolean): Page? {
        Log.d(TAG, "getPage:  $bookPosition , revers = $revers, recalc = $recalc")

        var restultPage: Page?
        if ( pages.isEmpty() || recalc || !isCurrentSectionLoaded(bookPosition)) {
            loadPages(pageView, bookPosition.section, recalc)
        }

         restultPage = findPage(bookPosition.offSet)

         if ( restultPage == null) restultPage = section(pageView, bookPosition, restultPage, revers, recalc)

        return restultPage
    }

    private fun isCurrentSectionLoaded(bookPosition: BookPosition) =
            (bookPosition.section >= pages.first().startBookPosition.section
                    && bookPosition.section <= pages.last().endBookPosition.section)

    private fun section(pageView: TextView, bookPosition: BookPosition, page: Page?, revers: Boolean, recalc: Boolean): Page? {
        var resultPage = page
        var section = bookPosition.section
        while (resultPage == null) {
            if (!revers) section++
            else section--

            section = max(0, section)
//            section = min(book.getPageScheme()!!.sectionCount, section)
            if (section >= 0 && section <= book.getPageScheme()!!.sectionCount) {
                loadPages(pageView, section, recalc)
                resultPage = if (pages.isEmpty()) null else {
                    if (!revers) pages.first() else pages.last()
                }
            } else {
                if (pages.isNotEmpty()) {
                    if (section <= 0 && revers) {
                        resultPage = pages.first()
                    } else if (!revers && section >= book.getPageScheme()!!.sectionCount) {
                        resultPage = pages.last()
                    }
                }
            }
        }
        return resultPage
    }

    private fun findPage(offset: Int): Page? {
        if ( offset < 0) return null
        if ( pages.isEmpty() ) return null
        if ( offset > pages.last().endBookPosition.offSet) return null

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

    private fun loadPages(pageView: TextView, section: Int, recalc: Boolean) {
        if (section >= 0 && section <= book.getPageScheme()!!.sectionCount) {
            val html = book.getPageBody(section)
            if ( html != null) {
                splitPages(pageView, book, section, html, reloadFonts = recalc)
            }
        }
    }

    companion object {
        const val TAG = "PageLoader"
    }
}