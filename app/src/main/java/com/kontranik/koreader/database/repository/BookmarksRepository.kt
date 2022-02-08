package com.kontranik.koreader.database.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.kontranik.koreader.database.BooksRoomDatabase

import com.kontranik.koreader.database.dao.BookmarksDao
import com.kontranik.koreader.model.Bookmark


internal class BookmarksRepository(context: Context) {
    private val mBookmarksDao: BookmarksDao

    init {
        val db: BooksRoomDatabase = BooksRoomDatabase.getDatabase(context)
        mBookmarksDao = db.bookmarksDao()
    }

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