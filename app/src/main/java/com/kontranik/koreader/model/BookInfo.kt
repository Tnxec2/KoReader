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
        var result: String = ""
        if (authors != null) {
            for (author in authors!!) {
                if (!result.isEmpty()) result += ", "
                result += author.firstname + " " + author.lastname
            }
        }
        return result
    }

    fun parseAuthorsFromString(authorsString: String) {
        if ( authors == null) authors = mutableListOf()
        authors!!.clear()

        var firstname: String? = null
        var middlename: String? = null
        var lastname: String? = null
        for ( a in authorsString.split(", ")) {
            val namen = a.split(" ")

            if ( namen.size == 3) {
                lastname = namen[2]
                middlename = namen[1]
                firstname = namen[0]
            } else if ( namen.size == 2) {
                lastname = namen[1]
                firstname = namen[0]
            } else if ( namen.size == 1) {
                firstname = namen[0]
            }
            val author = Author(firstname, middlename, lastname)

        }
    }

    override fun toString(): String {
        return "BookInfo. \n Title: " + title + "\n Authors: " + authorsAsString() + "\n path: " + path
    }
}