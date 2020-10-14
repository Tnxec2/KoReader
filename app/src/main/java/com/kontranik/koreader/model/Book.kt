package com.kontranik.koreader.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.kontranik.koreader.test.PageSplitterOne
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import nl.siegmann.epublib.domain.Book as EpubBook

class Book(var fileLocation: String?) {

    val TAG = "Book"
    var countPages: Int

    var pageSplitter: PageSplitterOne? = null

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
                if (page == 0) {
                    val cover = eBook!!.coverImage
                    if (cover != null) {
                        val coverData = cover.data
                        val bitmap = BitmapFactory.decodeByteArray(coverData, 0, coverData.size)
                        val html = "<html><body style=\"text-align: center\"><img src='{IMAGE_PLACEHOLDER}' style=\"height: auto;  width: auto;  max-width: 300px;  max-height: 300px; \" /></body></html>"

                        // Convert bitmap to Base64 encoded image for web
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                        val byteArray = byteArrayOutputStream.toByteArray()
                        val imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
                        val image = "data:image/png;base64,$imgageBase64"

                        // Use image for the img src parameter in your html and load to webview
                        data = html.replace("{IMAGE_PLACEHOLDER}", image)
                        return data
                        //txtPager.setText(currentPageNumber + " of " + maxPage);
                    } else {
                        getEpubPage(1)
                    }
                } else {
                    data = String(eBook!!.contents[page].data)
                    return data
                    //txtPager.setText(currentPageNumber + " of " + maxPage);
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun loadPage(page: Page, pageSplitter: PageSplitterOne ): Page {

        pageSplitter.clear()

        var bookPage = page.startCursor.bookPage
        var lastElement: Int = 0

        Log.d(TAG, "startWordIndex: " + page.startCursor.word)

        while (bookPage < countPages) {
            val bookPageText = getEpubPage(bookPage)
            val document = Jsoup.parse(bookPageText)
            val elements = document.body().select("*")
            lastElement = if ( bookPage == page.startCursor.bookPage) page.startCursor.pageElement else 0

            while (lastElement < elements.size) {

                val element = elements[lastElement]

                val startParagraph = if ( bookPage == page.startCursor.bookPage) page.startCursor.paragraph else 0
                val startWord = if ( bookPage == page.startCursor.bookPage && lastElement == page.startCursor.pageElement) page.startCursor.word else 0

                pageSplitter!!.append(getLine(element), startParagraph, startWord)
                if ( pageSplitter!!.page != null) break
                lastElement++
            }

            if ( pageSplitter!!.page != null) break
            bookPage++
        }

        Log.d(TAG, "*** pageEnde *** wordIndex: " + pageSplitter!!.wordIndex)

        page.endCursor.bookPage = bookPage
        page.endCursor.pageElement = lastElement
        page.endCursor.paragraph = pageSplitter!!.paragraphIndex
        page.endCursor.word = pageSplitter!!.wordIndex
        page.content = pageSplitter!!.page
        return page
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun loadPageRevers(page: Page, pageSplitter: PageSplitterOne): Page {
        pageSplitter.clear()

        var bookPage = page.startCursor.bookPage
        var lastElement: Int = 0
        Log.d(TAG, "startWordIndex: " + page.startCursor.word)

        while (bookPage >= 0) {
            val bookPageText = getEpubPage(bookPage)
            val document = Jsoup.parse(bookPageText)
            val elements = document.body().select("*")
            lastElement = if (bookPage == page.startCursor.bookPage) page.startCursor.pageElement else 0
            while (lastElement >= 0) {
                val element = elements[lastElement]
                val startParagraph = if (bookPage == page.startCursor.bookPage) page.startCursor.paragraph else 0
                val startWord = if (bookPage == page.startCursor.bookPage && lastElement == page.startCursor.pageElement) page.startCursor.word else 0

                pageSplitter!!.appendRevers(getLine(element), startParagraph, startWord)
                if (pageSplitter!!.page != null) break
                lastElement++
            }

            if (pageSplitter!!.page != null) break
            bookPage++
        }
        Log.d(TAG, "*** pageEnde *** wordIndex: " + pageSplitter!!.wordIndex)
        page.endCursor.bookPage = bookPage
        page.endCursor.pageElement = lastElement
        page.endCursor.paragraph = pageSplitter!!.paragraphIndex
        page.endCursor.word = pageSplitter!!.wordIndex
        page.content = pageSplitter!!.page
        return page
    }

    private fun getLine(element: Element): Line {
        val tag = element.normalName()
        //Log.d(TAG, el);
        Log.d(TAG, element.ownText());
        val myStyle = MyStyle.getFromString(tag)
        var text: String

        if (myStyle == MyStyle.Other) {
            text = "$tag::" + element.ownText()
        } else {
            text = element.ownText()
        }
        return Line( text, myStyle)
    }
}