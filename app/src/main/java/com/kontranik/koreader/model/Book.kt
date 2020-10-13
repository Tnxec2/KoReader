package com.kontranik.koreader.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import nl.siegmann.epublib.epub.EpubReader
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import nl.siegmann.epublib.domain.Book as EpubBook

class Book(var fileLocation: String?) {
    var countPages: Int
    var currentPageNumber: Int
    var currentPageString: String? = null
    var currentElement: Int
    var position: Int
    var eBook: EpubBook? = null
    var mEpubReader: EpubReader? = EpubReader()

    init {
        countPages = 0
        currentPageNumber = 0
        currentElement = 0
        position = 0
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

    fun getPage(): String? {
        if (currentPageNumber < 0 || currentPageNumber > countPages - 1) return null
        var data: String? = null
        try {
            if (eBook != null) {
                if (currentPageNumber == 0) {
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
                        currentPageNumber = 1
                        getPage()
                    }
                } else {
                    data = String(eBook!!.contents[currentPageNumber].data)
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
}