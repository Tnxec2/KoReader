package com.kontranik.koreader.database.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.dao.BookStatusDao
import com.kontranik.koreader.model.BookStatus

internal class BookStatusRepository(context: Context) {
    private val mBookStatusDao: BookStatusDao

    init {
        val db: BooksRoomDatabase = BooksRoomDatabase.getDatabase(context)
        mBookStatusDao = db.bookStatusDao()
    }

    suspend fun allBookStatus(): List<BookStatus> {
        return mBookStatusDao.getAll()
    }

    fun insert(bookStatus: BookStatus) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mBookStatusDao.insert(bookStatus) }
    }

    suspend fun getBookStatusByPath(path: String): BookStatus? {
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

    fun getLastOpened(lastOpenedCount: Int): LiveData<List<BookStatus>> {
        return mBookStatusDao.getLastOpened(lastOpenedCount)
    }

    fun delete(id: Long) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mBookStatusDao.delete(id) }
    }


}
