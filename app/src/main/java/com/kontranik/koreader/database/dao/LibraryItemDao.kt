package com.kontranik.koreader.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kontranik.koreader.database.model.LibraryItem
import com.kontranik.koreader.database.model.LibraryItemHelper
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(libraryItem: LibraryItem)

    @Update
    fun update(libraryItem: LibraryItem)

    @Query("DELETE FROM ${LibraryItemHelper.TABLE} where ${LibraryItemHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM ${LibraryItemHelper.TABLE}")
    fun getAll(): LiveData<List<LibraryItemWithAuthors>>

    @get:Query("SELECT SUBSTR(${LibraryItemHelper.COLUMN_TITLE}, 0, 1) as firstchar FROM ${LibraryItemHelper.TABLE} ORDER BY firstchar")
    val getAllGroupedTitles: LiveData<List<String>>

    @Transaction
    @Query("SELECT * FROM ${LibraryItemHelper.TABLE} where ${LibraryItemHelper.COLUMN_TITLE} LIKE :titleStart")
    fun getByTitleStart(titleStart: String): Flow<List<LibraryItemWithAuthors>>

    // @Transaction
//    @Query("SELECT * FROM ${LibraryItemHelper.TABLE} " +
//            "JOIN libraryItemToAuthorCrossRef ON libraryItemToAuthorCrossRef.libraryItemId = ${LibraryItemHelper.TABLE}.${LibraryItemHelper.COLUMN_ID} " +
//            "where libraryItemToAuthorCrossRef.authorId := authorId")
//    fun getByAuthor(authorId: Long): Flow<List<LibraryItemWithAuthors>>

}
