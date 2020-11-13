package com.kontranik.koreader.model

import android.graphics.Bitmap


class BookInfo(
        val title: String?,
        val cover: ByteArray?,
        val authors: List<Author>?,
        val filename: String,
        val path: String
) {

    fun authorsAsString() : String {
        var result: String = ""
        if (authors != null) {
            for (author in authors) {
                if (!result.isEmpty()) result += ", "
                result += author.firstname + " " + author.lastname
            }
        }
        return result
    }
}