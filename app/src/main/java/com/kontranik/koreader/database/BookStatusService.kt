package com.kontranik.koreader.database

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.BookStatus
import com.kontranik.koreader.utils.FileHelper
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class BookStatusService(var adapter: BookStatusDatabaseAdapter) {

    fun savePosition(book: Book) {
        adapter.open()
        val bookStatus = adapter.getBookStatusByPath(book.fileLocation)
        if ( bookStatus == null) {
            adapter.insert(BookStatus(book))
        } else {
            val bookPosition = if ( book.curPage == null) BookPosition() else BookPosition(book.curPage!!.startBookPosition)
            bookStatus.updatePosition(bookPosition)
            adapter.update(bookStatus)
        }
        adapter.close()
    }

    fun updateLastOpenTime(book: Book) {
        adapter.open()
        val bookStatus = adapter.getBookStatusByPath(book.fileLocation)
        if ( bookStatus != null ) {
            adapter.updateLastOpenTime(bookStatus.id, Date().time)
        } else {
            adapter.insert(BookStatus(book))
        }
        adapter.close()
    }

    fun getPosition(path: String): BookPosition? {
        adapter.open()
        val bookStatus = adapter.getBookStatusByPath(path)
        adapter.close()
        return if ( bookStatus == null )
            null
        else {
            BookPosition(bookStatus.position_section, bookStatus.position_offset)
        }
    }

    fun getLastOpened(count: Int): MutableList<BookStatus> {
        adapter.open()
        val result = adapter.getLastOpened(count)
        adapter.close()
        return result
    }

    fun delete(id: Long) {
        adapter.open()
        adapter.delete(id)
        adapter.close()
    }

    fun cleanup(context: Context) {
        adapter.open()
        val list = adapter.allBookStatus
        var file: File
        for( i in 0 until list.size) {
            val item = list.get(i)
            Log.d(TAG, "cleanup: " + item.path)
            if ( item.path == null ) {
                adapter.delete(item.id!!)
            } else {
                try {
                    if ( ! FileHelper.contentFileExist(context, item.path)) {
                        adapter.delete(item.id!!)
                    }
                } catch (e: FileNotFoundException) {
                    adapter.delete(item.id!!)
                }
            }
        }
        Log.d(TAG, "cleanup: finish")
        adapter.close()
    }

    companion object {
        private val TAG = "BookStatusService"
    }
}