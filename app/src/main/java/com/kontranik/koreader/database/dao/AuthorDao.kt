package com.kontranik.koreader.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.AuthorHelper

@Dao
interface AuthorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(author: Author)

    @Update
    fun update(author: Author)

    @Query("DELETE FROM ${AuthorHelper.TABLE} where ${AuthorHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @get:Query("SELECT * FROM ${AuthorHelper.TABLE}")
    val getAll: LiveData<List<Author>>

    @Transaction
    @Query("SELECT * FROM ${AuthorHelper.TABLE}")
    fun getAuthorsWithLibraryItems(): List<Author>

    @get:Query("SELECT SUBSTR(${AuthorHelper.COLUMN_FIRSTNAME}, 0, 1) as firstchar FROM ${AuthorHelper.TABLE} ORDER BY firstchar")
    val getAllGroupedAuthors: LiveData<List<String>>
}
