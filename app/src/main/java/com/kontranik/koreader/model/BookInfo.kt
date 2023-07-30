package com.kontranik.koreader.model

import android.graphics.Bitmap
import com.kontranik.koreader.utils.FileItem
import java.io.Serializable


class BookInfo(
        var title: String?,
        var cover: Bitmap?,
        var authors: MutableList<Author>?,
        val filename: String,
        val path: String,
        var annotation: String
) : Serializable {
    constructor(fileItem: FileItem) : this(title = fileItem.name, cover = null, authors = null, filename = fileItem.name, path = fileItem.path, annotation = "")

    fun authorsAsString() : String {
        var result = ""
        if (authors != null) {
            authors?.forEach { author ->
                if (result.isNotEmpty()) result += ", "
                result += author.firstname + " " + author.lastname
            }
        }
        return result
    }

    override fun toString(): String {
        return "BookInfo. \n Title: " + title + "\n Authors: " + authorsAsString() + "\n path: " + path
    }
}