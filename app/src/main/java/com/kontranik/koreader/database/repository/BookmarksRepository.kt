package com.kontranik.koreader.database.repository

import androidx.lifecycle.LiveData
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.dao.BookmarksDao
import com.kontranik.koreader.database.model.Bookmark


class BookmarksRepository(private val mBookmarksDao: BookmarksDao) {

    fun insert(bookmark: Bookmark) {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            mBookmarksDao.insert(bookmark)
        }
    }

    fun getByPath(path: String): LiveData<List<Bookmark>> {
        return mBookmarksDao.getByPath(path)
    }

    fun delete(id: Long) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mBookmarksDao.delete(id) }
    }
}