package com.kontranik.koreader.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.AuthorHelper
import com.kontranik.koreader.database.model.LibraryItem
import com.kontranik.koreader.database.model.LibraryItemAuthorsCrossRef
import com.kontranik.koreader.database.model.LibraryItemHelper
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import kotlinx.coroutines.flow.Flow


@Dao
interface LibraryItemDao {

    @RawQuery(observedEntities = [LibraryItem::class])
    fun getPagedLibraryItemViaQuery(query: SupportSQLiteQuery): PagingSource<Int, LibraryItemWithAuthors>

    fun getPage(
        author: Author?,
        searchText: String?): PagingSource<Int, LibraryItemWithAuthors> {

        val args: MutableList<Any?> = mutableListOf()

        val where = StringBuilder("")
        if (author != null) {
            if (author.id != null) {
                where.append(
                    " WHERE ${LibraryItemHelper.COLUMN_ID} IN " +
                            "(SELECT ${LibraryItemHelper.COLUMN_ID} FROM libraryItemToAuthorCrossRef WHERE authorid = ? )"
                )
                args.add(author.id)
            } else {
                where.append(
                    " WHERE ${LibraryItemHelper.COLUMN_ID} IN " +
                            "( SELECT ${LibraryItemHelper.COLUMN_ID} FROM libraryItemToAuthorCrossRef WHERE " +
                            " authorid IN ( SELECT ${AuthorHelper.COLUMN_ID} FROM ${AuthorHelper.TABLE}" +
                            " WHERE ${AuthorHelper.COLUMN_FIRSTNAME} IS ? " +
                            " AND ${AuthorHelper.COLUMN_MIDDLENAME} IS ? " +
                            " AND ${AuthorHelper.COLUMN_LASTNAME} IS ?  " +
                            " ) )"
                )
                args.add(author.firstname)
                args.add(author.middlename)
                args.add(author.lastname)
            }
        }
        if (searchText != null) {
            if (where.isBlank()) where.append(" WHERE ")
            else where.append(" AND ")
            where.append(" LOWER(${LibraryItemHelper.COLUMN_TITLE}) LIKE LOWER(?) ")
            args.add("%$searchText%")
        }
        val order = " ORDER BY ${LibraryItemHelper.COLUMN_TITLE}"
        val statement = "SELECT * FROM ${LibraryItemHelper.TABLE} $where $order"

        val argArray = arrayOfNulls<Any>(args.size)
        args.forEachIndexed { index, element ->
            argArray[index] = element
        }
        val query = SimpleSQLiteQuery(statement, argArray)

        return getPagedLibraryItemViaQuery(query)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(libraryItem: LibraryItem): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCrossRef(libraryItemAuthorsCrossRef: LibraryItemAuthorsCrossRef)

    @Update
    fun update(libraryItem: LibraryItem)

    @Query("DELETE FROM ${LibraryItemHelper.TABLE} where ${LibraryItemHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @Query("DELETE FROM libraryItemToAuthorCrossRef where libraryitemid = :libraryitemid")
    fun deleteCrossRefLibraryItem(libraryitemid: Long)

    @Query("DELETE FROM ${LibraryItemHelper.TABLE}")
    fun deleteAll()

    @Query("DELETE FROM libraryItemToAuthorCrossRef")
    fun deleteAllCrossRef()

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

    @Query("SELECT COUNT(${LibraryItemHelper.COLUMN_ID}) FROM ${LibraryItemHelper.TABLE} WHERE ${LibraryItemHelper.COLUMN_ID} " +
            " IN (SELECT libraryitemid FROM libraryItemToAuthorCrossRef WHERE authorid = :authorid )")
    fun getCountByAuthorId(authorid: Long): Long
}
