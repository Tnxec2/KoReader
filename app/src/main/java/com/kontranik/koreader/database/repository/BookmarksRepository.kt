package com.kontranik.koreader.database.repository

import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.dao.BookmarksDao
import com.kontranik.koreader.database.model.Bookmark
import kotlinx.coroutines.flow.Flow


class BookmarksRepository(private val mBookmarksDao: BookmarksDao) {

    fun insert(bookmark: Bookmark) {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            mBookmarksDao.insert(bookmark)
        }
    }

    fun getByPath(path: String): Flow<List<Bookmark>> {
        val result = mBookmarksDao.getByPath(path)
        return result
    }


    fun getByPathAndPosition(path: String, page: Int, startOffset: Int, endOffset: Int): Flow<List<Bookmark>> {
        val result = mBookmarksDao.getByPathAndPosition(path, page, startOffset, endOffset)
        return result
    }

    fun delete(id: Long) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mBookmarksDao.delete(id) }
    }
}