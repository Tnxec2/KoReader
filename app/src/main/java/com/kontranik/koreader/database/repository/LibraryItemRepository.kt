package com.kontranik.koreader.database.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.dao.LibraryItemDao
import com.kontranik.koreader.database.model.LibraryItem

import androidx.paging.PagingSource
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.LibraryItemAuthorsCrossRef
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import kotlinx.coroutines.flow.Flow

class LibraryItemRepository(private val mLibraryItemDao: LibraryItemDao) {

    @WorkerThread
    fun insert(libraryItem: LibraryItem): Long? {
        return mLibraryItemDao.insert(libraryItem)
    }

    @WorkerThread
    fun inserCrossRef(libraryItemAuthorsCrossRef: LibraryItemAuthorsCrossRef) {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            mLibraryItemDao.insertCrossRef(libraryItemAuthorsCrossRef)
        }
    }

    @WorkerThread
    fun getByPath(path: String): List<LibraryItem> = mLibraryItemDao.getByPath(path)

    @WorkerThread
    fun getByPathWithAuthors(path: String): List<LibraryItemWithAuthors> = mLibraryItemDao.getByPathWithAuthors(path)

    fun pageLibraryItem(author: Author?, searchText: String?): PagingSource<Int, LibraryItemWithAuthors> {
        return mLibraryItemDao.getPage(author, searchText)
    }

    fun getAllGroupedTitles(): LiveData<List<String>> {
        return mLibraryItemDao.getAllGroupedTitles
    }

    fun delete(id: Long) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mLibraryItemDao.delete(id) }
    }

    fun deleteCrossRefLibraryItem(libraryItem: LibraryItem) {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            libraryItem.id?.let { mLibraryItemDao.deleteCrossRefLibraryItem(it) }
        }
    }

    fun deleteAll() {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            mLibraryItemDao.deleteAll()
        }
    }
    fun deleteAllCrossRef() {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            mLibraryItemDao.deleteAllCrossRef()
        }
    }

    fun update(libraryItem: LibraryItem) {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            mLibraryItemDao.update(libraryItem)
        }
    }

    @WorkerThread
    fun getCountByAuthorId(authorId: Long): Long = mLibraryItemDao.getCountByAuthorId(authorId)
}