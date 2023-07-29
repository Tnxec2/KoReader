package com.kontranik.koreader.database.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.dao.LibraryItemDao
import com.kontranik.koreader.database.model.LibraryItem

import androidx.paging.PagingSource
import androidx.room.Query
import com.kontranik.koreader.database.model.LibraryItemHelper

class LibraryItemRepository(private val mLibraryItemDao: LibraryItemDao) {

    fun insert(libraryItem: LibraryItem) {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            val list = mLibraryItemDao.getByPath(libraryItem.path)
            if (list.isEmpty()) {
                mLibraryItemDao.insert(libraryItem)
            } else {
                val toUpdate = list[0]
                with(toUpdate)  {
                    cover = libraryItem.cover
                    title = libraryItem.title
                }
                mLibraryItemDao.update(toUpdate)
            }
        }
    }

    @WorkerThread
    fun getByPath(path: String): List<LibraryItem> = mLibraryItemDao.getByPath(path)

    fun pageLibraryItem(searchText: String?): PagingSource<Int, LibraryItem> {
        return mLibraryItemDao.getPage(searchText)
    }

    fun getAllGroupedTitles(): LiveData<List<String>> {
        return mLibraryItemDao.getAllGroupedTitles
    }

    fun delete(id: Long) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mLibraryItemDao.delete(id) }
    }

    fun deleteAll() {
        BooksRoomDatabase.databaseWriteExecutor.execute { mLibraryItemDao.deleteAll() }
    }
}