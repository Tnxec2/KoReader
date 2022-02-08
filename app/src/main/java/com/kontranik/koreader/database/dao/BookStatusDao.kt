package com.kontranik.koreader.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kontranik.koreader.database.BookStatusHelper
import com.kontranik.koreader.model.BookStatus

@Dao
interface BookStatusDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bookStatus: BookStatus)

    @Update
    fun update(bookStatus: BookStatus)

    @Query("DELETE FROM ${BookStatusHelper.TABLE} where ${BookStatusHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @Query("SELECT * FROM ${BookStatusHelper.TABLE}")
    suspend fun getAll(): List<BookStatus>

    @Query("SELECT * FROM ${BookStatusHelper.TABLE} ORDER BY ${BookStatusHelper.COLUMN_LAST_OPEN_TIME} DESC LIMIT :count")
    fun getLastOpened(count: Int): LiveData<List<BookStatus>>

    @Query("SELECT * FROM ${BookStatusHelper.TABLE} WHERE ${BookStatusHelper.COLUMN_PATH} = :path LIMIT 1")
    suspend fun getBookStatusByPath(path: String): BookStatus?

    @Query("SELECT * FROM ${BookStatusHelper.TABLE} WHERE ${BookStatusHelper.COLUMN_PATH} = :path LIMIT 1")
    fun getLiveDataBookStatusByPath(path: String): LiveData<BookStatus?>

    @Query("UPDATE ${BookStatusHelper.TABLE} SET ${BookStatusHelper.COLUMN_LAST_OPEN_TIME} = :lastOpenTime WHERE ${BookStatusHelper.COLUMN_ID} = :id")
    fun updateLastOpenTime(id: Long?, lastOpenTime: Long)


}
