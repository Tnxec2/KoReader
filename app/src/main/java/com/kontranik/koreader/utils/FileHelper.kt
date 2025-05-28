package com.kontranik.koreader.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.kontranik.koreader.parser.EbookHelper

object FileHelper {

    private const val TAG = "FileHelper"

    const val BACKDIR = ".."

    fun getFileListDC(context: Context, documentFilePath: String): List<FileItem> {
        val mUri = documentFilePath.toUri()
        val resolver: ContentResolver = context.contentResolver
        val uriId = DocumentsContract.getDocumentId(mUri)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(mUri, uriId)
        val resultF: ArrayList<Triple<Uri, String, String>> = ArrayList()

        val resultFiles = mutableListOf<FileItem>()

        val dirs: MutableList<FileItem> = mutableListOf()
        val files: MutableList<FileItem> = mutableListOf()

        val parentUri = documentFilePath.substringBeforeLast("%2F", documentFilePath.substringBeforeLast("%3A"))
        val isRoot = documentFilePath.contains("%3A")

        resultFiles.add(
            FileItem(
                image = ImageEnum.Parent,
                name = BACKDIR,
                path = Uri.decode(documentFilePath.split("/").last()).toString(),
                uriString = parentUri.toString(),
                isDir = true,
                isRoot = isRoot,
                bookInfo = null,
                isStorage = false)
        )

        resolver.query(
            childrenUri, arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE
            ), null, null, null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val documentId = cursor.getString(0)
                val documentUri = DocumentsContract.buildDocumentUriUsingTree(mUri, documentId)
                val name = cursor.getString(1)
                val mimeType = cursor.getString(2)
                val path = getPath(documentUri)
                if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                    dirs.add(FileItem(ImageEnum.Dir, name, path, documentUri.toString(), true, false, null))
                } else {
                    when {
                        EbookHelper.isEpub(name) -> files.add(FileItem(ImageEnum.Epub, name, path, documentUri.toString(), false, false, null))
                        EbookHelper.isFb2(name) -> files.add(FileItem(ImageEnum.Fb2, name, path, documentUri.toString(), false, false, null))
                    }
                }
            }
        }

        resultFiles.addAll(dirs.sortedBy { it.name })
        resultFiles.addAll(files.sortedBy { it.name })

        return resultFiles
    }

    fun getPath(uri: Uri): String {
        var path = uri.pathSegments.last()
        var iP = path.lastIndexOf("%3A")
        if ( iP > 0) path = path.substring(iP + 3)
        iP = path.lastIndexOf(":")
        if ( iP > 0) path = path.substring(iP + 1)
        return  path
    }

    fun contentFileExist(context: Context, uriPath: String?): Boolean {
        if ( uriPath == null) return false
        return try {
            val fileInputStream = context.contentResolver.openInputStream(uriPath.toUri())
            if ( fileInputStream != null) {
                fileInputStream.close()
                true
            } else {
                false
            }
        } catch ( e: Exception ) {
            false
        }
    }

    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if ( nameIndex >= 0) result = cursor.getString(nameIndex)
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = getNameFromPath(getPath(uri))
        }
        return result
    }

    private fun getNameFromPath(path: String): String {
        var result: String = path
        val cut = result.lastIndexOf('/')
        if (cut != -1) {
            result = result.substring(cut + 1)
        }
        return result
    }


}