package com.kontranik.koreader.model

import android.R.attr.maxHeight
import android.R.attr.maxWidth
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.annotation.RequiresApi
import com.kontranik.koreader.test.PageSplitterOne
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.math.max
import nl.siegmann.epublib.domain.Book as EpubBook


class Book(private var context: Context, var fileLocation: String?) {

    private val TAG = "Book"


    var countPages: Int

    private var eBook: EpubBook? = null
    private var mEpubReader: EpubReader? = EpubReader()

    init {
        countPages = 0
        loadBook()
    }

    private fun loadBook() {
        try {
            val fileInputStream = FileInputStream(fileLocation)
            eBook = mEpubReader?.readEpub(fileInputStream)
            val mSize = eBook?.contents?.size
            if ( mSize != null) {
                countPages = mSize
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getEpubPage(page: Int): String? {
        if (page < 0 || page > countPages - 1) return null
        val data: String?
        try {
            if (eBook != null) {
                if (page > 0)  {
                    data = String(eBook!!.contents[page].data)
                    return data
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun loadPage(page: Page, pageSplitter: PageSplitterOne): Page {

        pageSplitter.clear()

        var bookPage = page.startCursor.bookPage
        var lastElement = 0

        // Log.d(TAG, "startWordIndex: " + page.startCursor.word)

        while (bookPage > 0 && bookPage < countPages) {
            val bookPageText = getEpubPage(bookPage)
            val document = Jsoup.parse(bookPageText)
            val elements = document.body().select("*")
            lastElement = if ( bookPage == page.startCursor.bookPage) page.startCursor.pageElement else 0

            while (lastElement < elements.size) {

                val element = elements[lastElement]

                val startParagraph = if ( bookPage == page.startCursor.bookPage) page.startCursor.paragraph else 0
                val startWord = if ( bookPage == page.startCursor.bookPage && lastElement == page.startCursor.pageElement) page.startCursor.word else 0

                pageSplitter.append(getLine(element), startParagraph, startWord)
                if ( pageSplitter.page != null) break
                lastElement++
            }

            if ( pageSplitter.page != null) break
            bookPage++
        }

        // Log.d(TAG, "*** pageEnde *** wordIndex: " + pageSplitter.wordIndex)
        if ( bookPage == 0 ) {
            coverPage(page, pageSplitter.pageWidth, pageSplitter.pageHeight)
        } else {
            page.endCursor.bookPage = bookPage
            page.endCursor.pageElement = lastElement
            page.endCursor.paragraph = pageSplitter.paragraphIndex
            page.endCursor.word = pageSplitter.wordIndex
            page.content = pageSplitter.page
        }
        return page
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun loadPageRevers(page: Page, pageSplitter: PageSplitterOne): Page? {

        if ( page.endCursor.bookPage == 0 && page.endCursor.pageElement == 0 && page.endCursor.paragraph == 0 && page.endCursor.word == 0) return  null

        pageSplitter.clear()

        var bookPage = page.endCursor.bookPage
        var lastElement = 0
        // Log.d(TAG, "startWordIndex: " + ( page.endCursor.word - 1) )

        while (bookPage > 0) {
            val bookPageText = getEpubPage(bookPage)
            val document = Jsoup.parse(bookPageText)
            val elements = document.body().select("*")
            lastElement = if (bookPage == page.endCursor.bookPage) page.endCursor.pageElement else elements.size-1
            while (lastElement > 0) {
                val element = elements[lastElement]
                val startParagraph = if (bookPage == page.endCursor.bookPage) page.endCursor.paragraph else null
                val startWord = if (bookPage == page.endCursor.bookPage && lastElement == page.endCursor.pageElement) max(page.endCursor.word - 1, 0)  else null

                pageSplitter.appendRevers(getLine(element), startParagraph, startWord)
                if (pageSplitter.page != null) break
                lastElement--
            }
            if (pageSplitter.page != null) break
            bookPage--
        }
        // Log.d(TAG, "*** pageEnde *** wordIndex: " + pageSplitter.wordIndex)

        if ( bookPage == 0 ) {
            coverPage(page, pageSplitter.pageWidth, pageSplitter.pageHeight)
        } else {
            page.startCursor.bookPage = max(bookPage, 0)
            page.startCursor.pageElement = max(lastElement, 0)
            page.startCursor.paragraph = max(pageSplitter.paragraphIndex, 0)
            page.startCursor.word = max(pageSplitter.wordIndex, 0)
            page.content = pageSplitter.page
        }
        return page
    }

    private fun coverPage(page: Page, width: Int, height: Int) {
        page.content = getCover(width, height)
        page.startCursor = Cursor(0)
        page.endCursor = Cursor(1)
    }

    private fun getCover(pageWidth: Int, pageHeight: Int): SpannableStringBuilder {
        val cover = eBook!!.coverImage
        if ( cover != null ) {
            val coverData = eBook!!.coverImage.data

            var bitmap = BitmapFactory.decodeByteArray(coverData, 0, coverData.size)
            bitmap = scaleBitmap(bitmap, pageWidth, pageHeight)

            val span = ImageSpan(context, bitmap, ImageSpan.ALIGN_BASELINE)
            val text = " "
            val ssb = SpannableStringBuilder(text)
            ssb.setSpan(span, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return ssb
        } else {
            val title = eBook!!.title
            val authors = eBook!!.metadata.authors
            val ssb = SpannableStringBuilder()
            ssb.append(Word(title, MyStyle.Title).data)
            val author = authors.first()
            ssb.append(Word(author.firstname + " " + author.lastname , MyStyle.Italic).data)
            return ssb
        }
    }

    private fun getLine(element: Element): Line {
        val tag = element.normalName()
        // Log.d(TAG, el);
        // Log.d(TAG, element.ownText())
        val myStyle = MyStyle.getFromString(tag)
        val text = if (myStyle == MyStyle.Other) {
                        "$tag::" + element.ownText()
                    } else {
                        element.ownText()
                    }
        return Line(text, myStyle)
    }

    private fun scaleBitmap(bm: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
        var bm = bm
        var width = bm.width.toFloat()
        var height = bm.height.toFloat()
        if (width > height) {
            // landscape
            val ratio = width / maxWidth
            width = maxWidth.toFloat()
            height = height / ratio
        } else if (height > width) {
            // portrait
            val ratio = height / maxHeight
            height = maxHeight.toFloat()
            width = width / ratio
        } else {
            // square
            height = maxHeight.toFloat()
            width = maxWidth.toFloat()
        }
        bm = Bitmap.createScaledBitmap(bm, width.toInt(), height.toInt(), true)
        return bm
    }
}