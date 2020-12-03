package com.kontranik.koreader.parser.epubreader

import android.graphics.Bitmap
import com.kontranik.koreader.model.*
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.ImageUtils

import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class EpubHelper(private val fileLocation: String) : EbookHelper {

    private var epubReader = EpubReader()
    private var epubBook: Book? = null

    override fun readBook() {
        epubBook = readBook(fileLocation)
    }

    private fun readBook(path: String): Book? {
        val bookFile = File(path)

        try {
            val fileInputStream = FileInputStream(bookFile)
            return epubReader.readEpub(fileInputStream)
            fileInputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null;
    }

    override fun getContentSize(): Int {
        return epubBook!!.contents.size
    }

    override fun getPage(page: Int): String? {
        val data = epubBook?.contents?.get(page)?.data
        return data?.let { String(it) }
    }

    override fun getPageByHref(href: String): String? {
        val data = epubBook!!.resources.getByHref(href)?.data
        return data?.let { String(it) }
    }

    override fun getImageByHref(href: String): ByteArray? {
        return epubBook!!.resources.getByHref(href)?.data
    }

    override fun getBookInfoTemporary(path: String): BookInfo? {
        val eb = readBook(path)
        if (eb != null) {
            val t = eb.title
            val coverImage = eb.coverImage
            var coverBitmap: Bitmap? = null
            if (coverImage != null) {
                if ( coverImage.data != null)  coverBitmap = ImageUtils.byteArrayToBitmap(coverImage.data)
            }
            return BookInfo(
                    title =  t,
                    cover = coverBitmap,
                    authors = getAuthors(eb),
                    path = path,
                    filename = File(path).name)
        } else {
            return null
        }
    }

    private fun getAuthors(eBook: Book): List<Author> {
        return eBook.metadata.authors.map {
            Author(it.firstname, middlename = null, it.lastname)
        }
    }

}