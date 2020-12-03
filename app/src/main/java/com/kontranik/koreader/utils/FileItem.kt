package com.kontranik.koreader.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.kontranik.koreader.model.BookInfo
import java.util.*

class FileItem(
        var image: ImageEnum,
        var name: String,
        var path: String,
        var isDir: Boolean,
        var isRoot: Boolean,
        var bookInfo: BookInfo? = null
        ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val fileItem = other as FileItem
        return path == fileItem.path
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun hashCode(): Int {
        return Objects.hash(path)
    }
}

internal class FileItemNameComparator : Comparator<FileItem> {
    override fun compare(o1: FileItem, o2: FileItem): Int {
        return o1.name.compareTo(o2.name)
    }
}
