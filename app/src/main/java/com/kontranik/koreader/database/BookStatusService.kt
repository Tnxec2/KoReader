package com.kontranik.koreader.database

import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.BookStatus
import java.io.File

class BookStatusService(var adapter: BookStatusDatabaseAdapter) {

    fun getByPath(path: String): BookStatus? {
        adapter.open()
        val result = adapter.getBookStatusByPath(path)
        adapter.close()
        return result
    }

    fun savePosition(path: String, bookPosition: BookPosition) {
        adapter.open()
        val bookStatus = adapter.getBookStatusByPath(path)
        if ( bookStatus == null) {
            adapter.insert(BookStatus(null, path, bookPosition.page, bookPosition.element, bookPosition.paragraph, bookPosition.symbol))
        } else {
            bookStatus.updatePosition(bookPosition)
            adapter.update(bookStatus)
        }
        adapter.close()
    }

    fun getPosition(path: String): BookPosition? {
        adapter.open()
        val bookStatus = adapter.getBookStatusByPath(path)
        adapter.close()
        return if ( bookStatus == null ) null else BookPosition(bookStatus.position_page, bookStatus.position_element, bookStatus.position_paragraph, bookStatus.position_symbol)
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