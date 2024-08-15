package com.kontranik.koreader.parser

import android.content.Context
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookPageScheme
import com.kontranik.koreader.parser.epubreader.EpubHelper
import com.kontranik.koreader.parser.fb2reader.FB2Helper
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils

interface EbookHelper {

    var bookInfo: BookInfo?
    var pageScheme: BookPageScheme

    fun readBook()
    fun getContentSize(): Int
    fun getPage(page: Int): String?
    fun getPageByHref(href: String): String?
    fun getImageByHref(href: String): ByteArray?
    fun getBookInfoTemporary(contentUri: String): BookInfo?
    fun getCoverPage(): String?

    companion object {
        fun getBookInfo(mContext: Context, contentUriPath: String, result: FileItem): BookInfo? {
            return if (isEpub(contentUriPath)) {
                EpubHelper(mContext, contentUriPath).getBookInfoTemporary(contentUriPath)
            } else if (isFb2(contentUriPath)) {
                FB2Helper(mContext, contentUriPath).getBookInfoTemporary(contentUriPath)
            } else {
                BookInfo(result)
            }
        }

        fun isEpub(contentUriPath: String): Boolean {
            return contentUriPath.endsWith(".epub", true)
                || contentUriPath.endsWith(".epub.zip",  true)
        }

        fun isFb2(contentUriPath: String): Boolean{
            return contentUriPath.endsWith(".fb2",  true)
                    || contentUriPath.endsWith(".fb2.zip",  true)
        }

        fun getBookInfoTemporary(context: Context, contentUriPath: String): BookInfo? {
            val bookInfo: BookInfo? = try {
                if (isEpub(contentUriPath)) {
                    EpubHelper(
                        context,
                        contentUriPath
                    ).getBookInfoTemporary(contentUriPath)
                } else if (isFb2(contentUriPath)) {
                    FB2Helper(context, contentUriPath).getBookInfoTemporary(
                        contentUriPath
                    )
                } else {
                    null
                }
            } catch (ignored: Exception) {
                null
            }
            bookInfo?.let {
                if (it.cover == null) bookInfo.cover = ImageUtils.getBitmap(context, ImageEnum.Ebook)
            }
            return bookInfo
        }

        fun getHelper(context: Context, contentUri: String): EbookHelper? {
            if (isEpub(contentUri)) {
                return EpubHelper(context, contentUri)
            } else if (isFb2(contentUri)) {
                return FB2Helper(context, contentUri)
            }
            return null
        }
    }
}
