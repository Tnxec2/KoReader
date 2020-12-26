package com.kontranik.koreader.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import com.kontranik.koreader.model.BookInfo
import java.util.*

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

