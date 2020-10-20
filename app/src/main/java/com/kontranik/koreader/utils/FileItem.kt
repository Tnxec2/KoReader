package com.kontranik.koreader.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class FileItem(var image: ImageEnum, var name: String, var path: String, var isDir: Boolean, var isRoot: Boolean) {
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val fileItem = o as FileItem
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
