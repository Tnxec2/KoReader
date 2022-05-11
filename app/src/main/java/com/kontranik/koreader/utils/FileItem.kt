package com.kontranik.koreader.utils

import com.kontranik.koreader.model.BookInfo

class FileItem(
        var image: ImageEnum,
        var name: String,
        var path: String,
        var uriString: String?,
        var isDir: Boolean,
        var isRoot: Boolean,
        var bookInfo: BookInfo? = null
        ) {

}

