package com.kontranik.koreader.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import com.kontranik.koreader.parser.EbookHelper
import androidx.core.net.toUri

object FileHelper {

    private const val TAG = "FileHelper"


    const val BACKDIR = ".."
    const val COLON_ENCODED = "%3A" // :
    const val SLASH_ENCODED = "%2F" // /


    fun getFileListDC(
        context: Context,
        documentFilePath: String,
        isRoot: Boolean): List<FileItem> {
        val mUri = documentFilePath.toUri()
        val resolver: ContentResolver = context.contentResolver
        val uriId = DocumentsContract.getDocumentId(mUri)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(mUri, uriId)
        val resultF: ArrayList<Triple<Uri, String, String>> = ArrayList()

        var c: Cursor? = null
        try {
            val requestedColumns = arrayOf(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_MIME_TYPE
            )
            c = resolver.query(childrenUri, requestedColumns, null, null, null)
            while (c!!.moveToNext()) {
                val documentId: String = c.getString(0)
                val documentUri = DocumentsContract.buildDocumentUriUsingTree(mUri,
                        documentId)
                resultF.add( Triple(documentUri, c.getString(1), c.getString(2)))
                Log.d(TAG, c.getString(1) + " " + c.getString(2))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed query: $e")
        } finally {
            c?.close()
        }

        val resultFiles = mutableListOf<FileItem>()

        //val documentFile = DocumentFile.fromSingleUri(context, mUri)

        val index = documentFilePath.lastIndexOf(SLASH_ENCODED)
        val index2 = documentFilePath.lastIndexOf(COLON_ENCODED)

        var parent = ""
        if ( index > 0)  {
            parent = documentFilePath.substring(0, index)
        } else if ( index2 > 0 ) {
            parent = documentFilePath.substring(0, index2)
        }
        val pUri = parent.toUri()

        resultFiles.add(
            FileItem(
                image = ImageEnum.Parent,
                name = BACKDIR,
                path = Uri.decode(documentFilePath.split("/").last()).toString(),
                uriString = pUri.toString(),
                isDir = true,
                isRoot = isRoot,
                bookInfo = null,
                isStorage = false)
        )
        val dirs: MutableList<FileItem> = mutableListOf()
        val files: MutableList<FileItem> = mutableListOf()

        for (i in resultF.indices) {
            val uriToLoad = resultF[i].first

            val uriString = uriToLoad.toString()
            val path = getPath(uriToLoad)
            //val name = getNameFromPath(path)
            val name = resultF[i].second

            if ( resultF[i].third == DocumentsContract.Document.MIME_TYPE_DIR) {
                dirs.add(FileItem(ImageEnum.Dir, name, path, uriString = uriString, isDir = true, isRoot = false, null))
            } else {
                if (EbookHelper.isEpub(name)) {
                    files.add(FileItem(ImageEnum.Epub, name, path, uriString = uriString, isDir = false, false, null))
                } else if (EbookHelper.isFb2(name)) {
                    files.add(FileItem(ImageEnum.Fb2, name, path, uriString = uriString, isDir = false, false, null))
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