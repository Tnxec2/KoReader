package com.kontranik.koreader.database

import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.BookStatus
import com.kontranik.koreader.model.Bookmark
import java.io.File

class BookmarkService(var adapter: BookmarksDatabaseAdapter) {

    fun getByPath(path: String): List<Bookmark> {
        adapter.open()
        val result = adapter.getBookmarskByPath(path)
        adapter.close()
        return result
    }

    fun addBookmark(bookmark: Bookmark) : Boolean {
        if ( bookmark.id != null) throw Exception("Id is null")
        var result = false
        adapter.open()
        val list = adapter.getBookmarskByPathAndPosition(bookmark.path, BookPosition(bookmark))
        if ( list.isEmpty() ) {
            adapter.insert(bookmark)
            result = true
        }
        adapter.close()
        return result
    }

    fun deleteBookmark(bookmark: Bookmark) {
        if ( bookmark.id == null) return
        adapter.open()
        adapter.delete(bookmark.id!!)
        adapter.close()
    }

    fun cleanup() {
        adapter.open()
        val list = adapter.allBookmarks
        var file: File
        for( i in 0 until list.size) {
            val item = list.get(i)
            file = File(item.path)
            if ( ! file.exists() ) {
                adapter.delete(item.id!!)
            }
        }
        adapter.close()
    }
}