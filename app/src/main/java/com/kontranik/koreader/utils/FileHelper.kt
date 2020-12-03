package com.kontranik.koreader.utils

import android.os.Environment
import java.io.File
import java.util.*

object FileHelper {

    const val BACKDIR = ".."

    fun getNameNoExt(filename: String): String {
        var resultname = filename
        val i = resultname.lastIndexOf('.')
        if (i > 0) resultname = resultname.substring(0, i)
        return resultname
    }

    fun getExt(filename: String): String { // FilenameUtils.getExtension(n)
        val i = filename.lastIndexOf('.')
        return if (i > 0) filename.substring(i + 1) else ""
    }

    val storageList: List<FileItem>
        get() {
            val result: MutableList<FileItem> = ArrayList()
            val externalStorageDirectory = Environment.getExternalStorageDirectory()
            result.add(FileItem(
                    ImageEnum.SD,
                    externalStorageDirectory.name,
                    externalStorageDirectory.path,
                    isDir = true,
                    isRoot = true, null))
            if (externalStorageDirectory.parent != null) {
                var files = File(externalStorageDirectory.parent).listFiles()
                var temp: Array<File?>? = null
                if (files != null && files.size > 0) {
                    for (file in files) {
                        if (file.isDirectory && file.canRead()) temp = file.listFiles()
                        if (temp != null && temp.size > 0) result.add(FileItem(
                                ImageEnum.SD, file.name, file.path, true, true, null))
                    }
                } else {
                    val p = File(externalStorageDirectory.parent).parent
                    if (p != null) {
                        files = File(p).listFiles()
                        if (files != null && files.size > 0) {
                            for (file in files) {
                                if (file.isDirectory && file.canRead()) {
                                    temp = file.listFiles()
                                    if (temp != null && temp.size > 0) result.add(
                                            FileItem(ImageEnum.SD, file.name, file.path, true, true, null))
                                }
                            }
                        }
                    }
                }
            }
            return result
        }

    fun getFileList(path: String?): List<FileItem> {
        val result: MutableList<FileItem> = ArrayList()
        if (path == null) return result
        val dir = File(path)
        if (!dir.isDirectory) return result
        result.add(FileItem(ImageEnum.Parent, BACKDIR, dir.parent, isDir = true, isRoot = false, null))
        val dirs = dir.listFiles { current, name ->
            val f = File(current, name)
            !f.isHidden && !name.startsWith(".") && f.isDirectory
        }
        val files = dir.listFiles { current, name ->
            val f = File(current, name)
            (!f.isHidden && !name.startsWith(".")
                    && (
                    name.endsWith(".epub", true)
                    || name.endsWith(".fb2", true)
                    || name.endsWith(".fb2.zip", true)
                    )
                    )
        }
        if (dirs != null && dirs.isNotEmpty()) {
            Arrays.sort(dirs)
            for (f in dirs) {
                if (f.isDirectory) {
                    result.add(FileItem(ImageEnum.Dir, f.name, f.path, f.isDirectory, isRoot = false, null))
                }
            }
        }
        if (files != null && files.isNotEmpty()) {
            Arrays.sort(files)
            for (f in files) {
                if ( f.name.endsWith(".epub", true) ) {
                    result.add(FileItem(ImageEnum.Epub, f.name, f.path, f.isDirectory, false, null))
                } else if (
                    f.name.endsWith(".fb2", true)
                    || f.name.endsWith(".fb2.zip", true)
                ) {
                    result.add(
                            FileItem(ImageEnum.Fb2, f.name, f.path, f.isDirectory, false, null))
                }
            }
        }
        return result
    }
}