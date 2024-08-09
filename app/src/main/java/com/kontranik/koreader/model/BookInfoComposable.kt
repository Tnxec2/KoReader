package com.kontranik.koreader.model

import androidx.compose.ui.graphics.ImageBitmap
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.utils.FileItem
import java.io.Serializable


data class BookInfoComposable(
        var title: String,
        var cover: ImageBitmap,
        var authors: MutableList<Author>,
        var authorsAsString: String,
        val filename: String,
        val path: String,
        var annotation: String
) : Serializable {
    init {
        authorsAsString = authors.joinToString("; ", transform = { it.asString() })
    }

    constructor(fileItem: FileItem, cover: ImageBitmap) : this(
        title = fileItem.name, cover = cover, authors = mutableListOf(), filename = fileItem.name, path = fileItem.path, annotation = "", authorsAsString = "")


    override fun toString(): String {
        return "BookInfo. \n Title: $title\n Authors: $authorsAsString\n path: $path"
    }
}