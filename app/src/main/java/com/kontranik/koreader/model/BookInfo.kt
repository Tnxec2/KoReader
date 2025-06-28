package com.kontranik.koreader.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.utils.FileItem
import java.io.Serializable


class BookInfo(
        var title: String?,
        var cover: Bitmap?,
        var authors: MutableList<Author>?,
        val filename: String,
        val path: String,
        var annotation: String,
        var sequenceName: String? = null,
        var sequenceNumber: String? = null,
) : Serializable {
    constructor(fileItem: FileItem) : this(title = fileItem.name, cover = null, authors = null, filename = fileItem.name, path = fileItem.path, annotation = "")

    var coverLoaded: Boolean = false

    fun authorsAsString() : String {
        var result = ""
        if (authors != null) {
            authors?.forEach { author ->
                if (result.isNotEmpty()) result += ", "
                result += author.asString()
            }
        }
        return result
    }

    override fun toString(): String {
        return "BookInfo. \n Title: " + title + "\n Authors: " + authorsAsString() + "\n path: " + path
    }
}

fun BookInfo.toBookInfoComposable(): BookInfoComposable {
    return BookInfoComposable(
        title = title ?: "",
        cover = cover?.asImageBitmap() ?: ImageBitmap(50, 100),
        authors = authors ?: mutableListOf(),
        authorsAsString = authorsAsString(),
        filename = filename,
        path = path,
        annotation = annotation,
        sequenceName = sequenceName ?: "",
        sequenceNumber = sequenceNumber ?: "",
    )
}