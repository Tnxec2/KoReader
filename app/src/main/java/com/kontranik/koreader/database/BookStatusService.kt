package com.kontranik.koreader.database

import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.BookStatus
import java.io.File

class BookStatusService(var adapter: BookStatusDatabaseAdapter) {

    fun savePosition(path: String, bookPosition: BookPosition) {
        adapter.open()
        val bookStatus = adapter.getBookStatusByPath(path)
        if ( bookStatus == null) {
            adapter.insert(
                    BookStatus(null, path, bookPosition.section,
                            bookPosition.offSet))
        } else {
            bookStatus.updatePosition(bookPosition)
            adapter.update(bookStatus)
        }
        adapter.close()
    }

    fun updateLastOpenTime(path: String) {
        adapter.open()
        val bookStatus = adapter.getBookStatusByPath(path)
        if ( bookStatus != null ) {
            bookStatus.updateLastOpenTime()
            adapter.update(bookStatus)
        } else {
            adapter.insert(BookStatus(path = path))
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

    fun cleanup() {
        adapter.open()
        val list = adapter.allBookStatus
        var file: File
        for( i in 0 until list.size) {
            val item = list.get(i)
            if ( item.path == null ) {
                adapter.delete(item.id!!)
            } else {
                file = File(item.path!!)
                if ( ! file.exists() ) {
                    adapter.delete(item.id!!)
                }
            }
        }
        adapter.close()
    }
}