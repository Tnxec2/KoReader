package com.kontranik.koreader.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.kontranik.koreader.database.model.LibraryItem
import com.kontranik.koreader.database.model.LibraryItemHelper
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryItemDao {

    @RawQuery(observedEntities = [LibraryItem::class])
    fun getPagedLibraryItemViaQuery(query: SupportSQLiteQuery): PagingSource<Int, LibraryItem>

    fun getPage(
        searchText: String?): PagingSource<Int, LibraryItem> {
        val where = StringBuilder("")
        if (searchText != null) {
            if (where.isBlank()) where.append(" WHERE ")
            else where.append(" AND ")
            where.append(" LOWER(title) LIKE LOWER('%$searchText%') ")
        }

        val statement = "SELECT * FROM ${LibraryItemHelper.TABLE} $where"
        val query = SimpleSQLiteQuery(statement)
        return getPagedLibraryItemViaQuery(query)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(libraryItem: LibraryItem)

    @Update
    fun update(libraryItem: LibraryItem)

    @Query("DELETE FROM ${LibraryItemHelper.TABLE} where ${LibraryItemHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @Query("DELETE FROM ${LibraryItemHelper.TABLE}")
    fun deleteAll()

    @Transaction
    @Query("SELECT * FROM ${LibraryItemHelper.TABLE}")
    fun getAll(): LiveData<List<LibraryItemWithAuthors>>

    @get:Query("SELECT SUBSTR(${LibraryItemHelper.COLUMN_TITLE}, 0, 1) as firstchar FROM ${LibraryItemHelper.TABLE} ORDER BY firstchar")
    val getAllGroupedTitles: LiveData<List<String>>

    @Query("SELECT * FROM ${LibraryItemHelper.TABLE} WHERE ${LibraryItemHelper.COLUMN_PATH} = :path")
    fun getByPath(path: String): List<LibraryItem>

    @Transaction
    @Query("SELECT * FROM ${LibraryItemHelper.TABLE} where ${LibraryItemHelper.COLUMN_TITLE} LIKE :titleStart")
    fun getByTitleStart(titleStart: String): Flow<List<LibraryItemWithAuthors>>

    // @Transaction
//    @Query("SELECT * FROM ${LibraryItemHelper.TABLE} " +
//            "JOIN libraryItemToAuthorCrossRef ON libraryItemToAuthorCrossRef.libraryItemId = ${LibraryItemHelper.TABLE}.${LibraryItemHelper.COLUMN_ID} " +
//            "where libraryItemToAuthorCrossRef.authorId := authorId")
//    fun getByAuthor(authorId: Long): Flow<List<LibraryItemWithAuthors>>

}
