package com.kontranik.koreader.database.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Transaction
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.dao.AuthorDao
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.LibraryItem
import com.kontranik.koreader.database.model.LibraryItemWithAuthors


class AuthorsRepository(private val mAuthorDao: AuthorDao) {

    fun pageAuthor(searchText: String?): PagingSource<Int, Author> {
        return mAuthorDao.getPage(searchText)
    }

    @WorkerThread
    fun insert(author: Author): Long? {
        return mAuthorDao.insert(author)
    }

    fun delete(id: Long) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mAuthorDao.delete(id) }
    }

    fun deleteCrossRefAuthor(author: Author) {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            author.id?.let { mAuthorDao.deleteCrossRefAuthor(it) }
        }
    }

    fun deleteAll() {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            mAuthorDao.deleteAll()
        }
    }

    @WorkerThread
    fun getByName(firstname: String?, middlename: String?, lastname: String?): List<Author> {
        return mAuthorDao.getByName(firstname, middlename, lastname)
    }
}