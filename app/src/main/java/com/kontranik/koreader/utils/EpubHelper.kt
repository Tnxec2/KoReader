package com.kontranik.koreader.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import com.kontranik.koreader.model.*

import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class EpubHelper(
        private val context: Context,
        private val fileLocation: String) : EbookHelper {

    var epubReader = EpubReader()
    var epubBook: Book? = null

    init {
        epubBook = getEBook(context, fileLocation)
    }

    private fun getEBook(context: Context, path: String?): Book? {
        if (fileLocation.endsWith(".zip")) {
            val zipFile = File(fileLocation)
            val targetDir = File(context.getExternalFilesDir(null), "cacheunzip")
            targetDir.mkdir()

            ZipHelper.unzip(zipFile, targetDir)
        }
        val bookFile = File(fileLocation)
        var result: Book? = null
        try {
            val fileInputStream = FileInputStream(bookFile)
            result = epubReader.readEpub(fileInputStream)
            fileInputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun getAuthors(eBook: Book): List<Author> {
        return eBook.metadata.authors.map {
            Author(it.firstname, it.lastname)
        }
    }

    fun getPage(page: Int): String? {
        val data = epubBook?.contents?.get(page)?.data
        return data?.let { String(it) }
    }

    fun pagesCount(): Int? {
        return epubBook?.contents?.size
    }

    fun getElementsForPage(page: Int): MutableList<AbstractElement> {
        val bookPageText = getPage(page) ?: return mutableListOf()
        val document = Jsoup.parse(bookPageText)
        val elements = document.body().select("*")
        val result = mutableListOf<AbstractElement>()
        for (e in elements) {
            AbstractElement(normalname = e.normalName(), text = e.ownText())
        }
        return result
    }

    fun getBookInfo(path: String): BookInfo? {
        if (epubBook != null) {
            val t = epubBook!!.title
            val coverImage = epubBook!!.coverImage
            var coverBitmap: Bitmap? = null
            if (coverImage != null) {
                if ( coverImage.data != null)  coverBitmap = ImageUtils.byteArrayToBitmap(coverImage.data)
            }
            return BookInfo(
                    title =  t,
                    cover = coverBitmap,
                    authors = getAuthors(epubBook!!),
                    path = path,
                    filename = File(path).name)
        } else {
            return null
        }
    }

    fun getCover(context: Context, path: String?): ByteArray? {
        if (path == null) return null
        val eBook = getEBook(context, path)
        val cover = eBook?.coverImage ?: return null
        return cover.data
    }

    fun getCover(pageWidth: Int, pageHeight: Int): SpannableStringBuilder {
        val cover = getCover(context, fileLocation)
        if ( cover != null ) {

            var bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.size)
            bitmap = ImageUtils.scaleBitmap(bitmap, pageWidth, pageHeight)

            val span = ImageSpan(context, bitmap, ImageSpan.ALIGN_BASELINE)
            val text = " "
            val ssb = SpannableStringBuilder(text)
            ssb.setSpan(span, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return ssb
        } else {
            val title = epubBook!!.title
            val authors = epubBook!!.metadata.authors
            val ssb = SpannableStringBuilder()
            val html = "<html><body><h1>%s</h1><p>%s<p></body></html>"
            val author = authors.first()
            val text = String.format(html, title!!, author.firstname + " " + author.lastname)
            ssb.append(Html.fromHtml(text))
            return ssb
        }
    }

}