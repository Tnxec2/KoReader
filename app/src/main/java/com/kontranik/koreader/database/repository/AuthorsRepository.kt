package com.kontranik.koreader.database.repository

import androidx.lifecycle.LiveData
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.dao.AuthorDao
import com.kontranik.koreader.database.model.Author


class AuthorsRepository(private val mAuthorDao: AuthorDao) {

    fun insert(author: Author) {
        BooksRoomDatabase.databaseWriteExecutor.execute {
            mAuthorDao.insert(author)
        }
    }

    fun getAll(): LiveData<List<Author>> {
        return mAuthorDao.getAll
    }

    fun delete(id: Long) {
        BooksRoomDatabase.databaseWriteExecutor.execute { mAuthorDao.delete(id) }
    }
}