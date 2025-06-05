package com.kontranik.koreader.utils

import androidx.documentfile.provider.DocumentFile
import com.kontranik.koreader.model.BookInfo

data class FileItem(
        var image: ImageEnum,
        var name: String,
        var path: String,
        var uriString: String?,
        var isDir: Boolean,
        var isRoot: Boolean,
        var bookInfo: BookInfo? = null,
        var isStorage: Boolean = false
) {
        constructor(documentsTree: DocumentFile) : this(
        image = ImageEnum.SD,
        name = documentsTree.name ?: documentsTree.uri.toString(),
        path = documentsTree.uri.pathSegments.last(),
        uriString = documentsTree.uri.toString(),
        isDir = true,
        isRoot = false,
        bookInfo = null,
        isStorage = true
        )
}

