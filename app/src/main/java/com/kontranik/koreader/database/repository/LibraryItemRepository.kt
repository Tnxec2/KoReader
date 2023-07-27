package com.kontranik.koreader.database.repository

import androidx.lifecycle.LiveData
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.dao.LibraryItemDao
import com.kontranik.koreader.database.model.LibraryItem


class LibraryItemRepository(private val mLibraryItemDao: LibraryItemDao) {

    fun insert(bookmark: LibraryItem) {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            mLibraryItemDao.insert(bookmark)
        }
    }

    fun getByPath(): LiveData<List<String>> {
        return mLibraryItemDao.getAllGroupedTitles
    }

    fun delete(id: Long) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mLibraryItemDao.delete(id) }
    }
}