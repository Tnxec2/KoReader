package com.kontranik.koreader.database.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.dao.BookStatusDao
import com.kontranik.koreader.database.model.BookStatus
import kotlinx.coroutines.flow.Flow

class BookStatusRepository(private val mBookStatusDao: BookStatusDao) {

    fun allBookStatus(): List<BookStatus> {
        return mBookStatusDao.getAll()
    }

    fun insert(bookStatus: BookStatus) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mBookStatusDao.insert(bookStatus) }
    }

    fun getBookStatusByPath(path: String): BookStatus? {
        return mBookStatusDao.getBookStatusByPath(path)
    }

    fun getLiveDataBookStatusByPath(path: String): LiveData<BookStatus?> {
        return mBookStatusDao.getLiveDataBookStatusByPath(path)
    }

    fun updateLastOpenTime(id: Long?, lastOpenTime: Long) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mBookStatusDao.updateLastOpenTime(id, lastOpenTime) }
    }

    fun update(bookStatus: BookStatus) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mBookStatusDao.update(bookStatus) }
    }

    fun getLastOpened(lastOpenedCount: Int): Flow<List<BookStatus>> {
        return mBookStatusDao.getLastOpened(lastOpenedCount)
    }

    fun delete(id: Long) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mBookStatusDao.delete(id) }
    }

    fun deleteOlderCount(count: Int) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mBookStatusDao.deleteOlderCount(count) }
    }
}
