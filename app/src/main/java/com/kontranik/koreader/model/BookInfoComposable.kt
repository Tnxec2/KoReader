package com.kontranik.koreader.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoDetails
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.BookStatus
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.ImageUtils.getBitmap
import java.io.Serializable
import java.net.URLDecoder


data class BookInfoComposable(
        var title: String,
        var cover: ImageBitmap,
        var authors: MutableList<Author> = mutableListOf(),
        var authorsAsString: String = authors.joinToString("; ", transform = { it.asString() }),
        val filename: String = "",
        val path: String,
        var annotation: String = ""
) : Serializable {

    override fun toString(): String {
        return "BookInfo. \n Title: $title\n Authors: $authorsAsString\n path: $path"
    }
}

fun BookStatus.toBookInfoComposable(altCover: ImageBitmap): BookInfoComposable {
    return BookInfoComposable(
        title = title ?: "",
        cover = cover?.let { it1 -> ImageUtils.getImage(it1) }?.asImageBitmap() ?: altCover,
        authors = mutableListOf(),
        authorsAsString = authors ?: "",
        filename = URLDecoder.decode(path),
        path = URLDecoder.decode(path),
        annotation = ""
    )
}

fun FileItem.toBookInfoComposable(cover: ImageBitmap): BookInfoComposable {
    return BookInfoComposable(
        title = name,
        cover = cover,
        authors = mutableListOf(),
        filename = name,
        path = path,
        annotation = "",
        authorsAsString = ""
    )
}

fun LibraryItemWithAuthors.toBookInfoComposable(cover: Bitmap): BookInfoComposable {
    return BookInfoComposable(
        title = libraryItem.title ?: "",
        cover = cover.asImageBitmap(),
        path = URLDecoder.decode(libraryItem.path),
        authors = authors.toMutableList(),
        authorsAsString = authors.joinToString("; ", transform = { it.asString() }),
    )
}

fun LibraryItemWithAuthors.toBookInfoDetails(cover: Bitmap?, annotation: String): BookInfoDetails {
    return BookInfoDetails(
        title = libraryItem.title ?: "",
        cover = cover,
        authors = authors.toList(),
        allAuthors = authors.joinToString("; ", transform = { it.asString() }),
        filename = libraryItem.path,
        path = libraryItem.path,
        annotation = annotation
    )
}