package com.kontranik.koreader.parser.fb2reader

import android.content.Context
import android.graphics.Bitmap
import com.kontranik.koreader.model.*
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.parser.fb2reader.model.FB2Scheme
import com.kontranik.koreader.utils.ImageUtils

import java.io.File

class FB2Helper(private val context: Context , fileLocation: String) : EbookHelper {

    private var fb2Reader: FB2Reader = FB2Reader(
       if ( context.externalCacheDir != null) context.externalCacheDir!!.absolutePath
       else context.filesDir.absolutePath, fileLocation)

    override fun readBook() {
        fb2Reader.readBook()
    }

    override fun getContentSize(): Int {
        return fb2Reader.fb2Scheme.sections.size
    }

    override fun getPage(page: Int): String? {
        return fb2Reader.getSectionHtml(page)
    }

    override fun getPageByHref(href: String): String? {
        return fb2Reader.getSectionHtml(href)
    }

    override fun getImageByHref(href: String): ByteArray? {
        return fb2Reader.getBinary(href)
    }

    override fun getBookInfoTemporary(path: String): BookInfo? {
        val tempReader = FB2Reader(context.filesDir.absolutePath, path)
        val tempScheme = tempReader.readScheme()
        if (tempScheme != null) {
            val t = tempScheme.description.titleInfo.booktitle
            val coverImage = tempScheme.cover
            var coverBitmap: Bitmap? = null
            if (coverImage != null) {
                if ( coverImage.contentsArray != null)
                    coverBitmap = ImageUtils.byteArrayToBitmap(coverImage.contentsArray)
            }
            return BookInfo(
                    title =  t,
                    cover = coverBitmap,
                    authors = getAuthors(tempScheme),
                    path = path,
                    filename = File(path).name)
        } else {
            return null
        }
    }

    private fun getAuthors(tempScheme: FB2Scheme): List<Author> {
        return tempScheme.description.titleInfo.authors.map {
            Author(it.firstname, it.middlename, it.lastname)
        }
    }

}