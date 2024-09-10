package com.kontranik.koreader.utils

import android.util.Log
import com.kontranik.koreader.compose.ui.settings.ThemeColors
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.Page
import com.kontranik.koreader.model.PageViewSettings
import kotlin.math.max

class PageLoader : PageSplitterHtml(){

    private lateinit var book: Book

    private lateinit var mPageViewSettings: PageViewSettings
    private lateinit var mColors: ThemeColors

    fun getPage(
        book2: Book?,
        pageViewSettings: PageViewSettings,
        colors: ThemeColors,
        pageIndex: Int?,
        bookPosition: BookPosition,
        revers: Boolean,
        recalc: Boolean): Page? {

        if (book2 == null) return null

        mPageViewSettings = pageViewSettings.copy()
        book = book2
        mColors = colors.copy()

        Log.d(TAG, "PageLoader.getPage: Index = $pageIndex, size pages = ${pages.size}, $bookPosition , revers = $revers, recalc = $recalc")

        if (pageIndex != null && pages.isNotEmpty()) {
            return if (pageIndex >= 0 && pageIndex < pages.size-1)
                pages[pageIndex]
            else {
                if (bookPosition.section == 0 && pageIndex < 0)
                   pages[0]
                else
                   section(bookPosition.section, revers = pageIndex < 0, recalc)
            }
        }

        if ( pages.isEmpty() || recalc || !isCurrentSectionLoaded(bookPosition)) {
            loadPages(bookPosition.section, recalc)
        }

        val restultPage: Page = findPage(bookPosition.offSet)?.apply {
            pageStartPosition.offSet = bookPosition.offSet
        } ?: section(bookPosition.section, revers, recalc)
        Log.d(TAG, "resultPage. start = ${restultPage.pageStartPosition}, end = ${restultPage.pageEndPosition}")
        return restultPage
    }

    private fun isCurrentSectionLoaded(bookPosition: BookPosition) =
            (bookPosition.section >= pages.first().pageStartPosition.section
                    && bookPosition.section <= pages.last().pageEndPosition.section)

    private fun section(initSection: Int, revers: Boolean, recalc: Boolean): Page {
        var resultPage: Page? = null
        var section = initSection
        while (resultPage == null) {
            section += if (revers) -1 else 1

            section = max(0, section)
//            section = min(book.getPageScheme()!!.sectionCount, section)
            if (section >= 0 && section <= book.getPageScheme()!!.sectionCount) {
                loadPages(section, recalc)
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
        println("section-resultPage: index: ${resultPage.pageIndex} start: ${resultPage.pageStartPosition}, end: ${resultPage.pageEndPosition}")
        return resultPage
    }

    private fun findPage(offset: Int): Page? {
        if ( offset < 0) return null
        if ( pages.isEmpty() ) return null
        if ( offset > pages.last().pageEndPosition.offSet) return null

        val result = pages.firstOrNull { page: Page ->
            offset in page.pageStartPosition.offSet .. page.pageEndPosition.offSet
        }
        Log.d(TAG, "findPage. offset = $offset, start = ${result?.pageStartPosition}, end = ${result?.pageEndPosition}")
        return result
    }

    private fun loadPages(section: Int, recalc: Boolean) {
        Log.d(TAG,"loadPages: $section of ${book.getPageScheme()!!.sectionCount}")
        if (section >= 0 && section <= book.getPageScheme()!!.sectionCount) {
            val html = book.getPageBody(section)
            if ( html != null) {
                splitPages(
                    pageViewSettings = mPageViewSettings,
                    themeColors = mColors,
                    book = book,
                    section = section,
                    html =  html,
                    reloadFonts = recalc)
            }
        }
    }

    companion object {
        const val TAG = "PageLoader"
    }
}