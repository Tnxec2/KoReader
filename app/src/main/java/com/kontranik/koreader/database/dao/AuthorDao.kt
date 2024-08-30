package com.kontranik.koreader.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.AuthorHelper

@Dao
interface AuthorDao {

    @RawQuery(observedEntities = [Author::class])
    fun getAuthorViaQuery(query: SupportSQLiteQuery): PagingSource<Int, Author>

    fun getPage(
        searchText: String?): PagingSource<Int, Author> {
        val args: MutableList<Any?> = mutableListOf()
        val where = StringBuilder("")
        if (searchText != null) {
            where.append(" WHERE ")
            where.append(" LOWER(${AuthorHelper.COLUMN_FIRSTNAME}) LIKE LOWER(?) ")
            where.append(" OR LOWER(${AuthorHelper.COLUMN_MIDDLENAME}) LIKE LOWER(?) ")
            where.append(" OR LOWER(${AuthorHelper.COLUMN_LASTNAME}) LIKE LOWER(?) ")
            args.add("%$searchText%")
            args.add("%$searchText%")
            args.add("%$searchText%")
        }
        val order = " ORDER BY ${AuthorHelper.COLUMN_LASTNAME}, ${AuthorHelper.COLUMN_FIRSTNAME}"
        val statement = "SELECT * FROM ${AuthorHelper.TABLE} $where $order"
        val argArray = arrayOfNulls<Any>(args.size)
        args.forEachIndexed { index, element ->
            argArray[index] = element
        }
        val query = SimpleSQLiteQuery(statement, argArray)
        return getAuthorViaQuery(query)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(author: Author): Long?

    @Update
    fun update(author: Author)

    @Query("DELETE FROM ${AuthorHelper.TABLE} where ${AuthorHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM ${AuthorHelper.TABLE}")
    fun getAuthorsWithLibraryItems(): List<Author>

    @Query("DELETE FROM ${AuthorHelper.TABLE}")
    fun deleteAll()

    @Query("DELETE FROM libraryItemToAuthorCrossRef where authorid = :authorid")
    fun deleteCrossRefAuthor(authorid: Long)

    @Query("SELECT * FROM ${AuthorHelper.TABLE} WHERE " +
            " ( ${AuthorHelper.COLUMN_FIRSTNAME} = :firstname or (${AuthorHelper.COLUMN_FIRSTNAME} is null and :firstname is null)) " +
            "AND " +
            " ( ${AuthorHelper.COLUMN_MIDDLENAME} = :middlename or (${AuthorHelper.COLUMN_MIDDLENAME} is null and :middlename is null)) " +
            "AND " +
            "${AuthorHelper.COLUMN_LASTNAME} = :lastname or (${AuthorHelper.COLUMN_LASTNAME} is null and :lastname is null) ")
    fun getByName(firstname: String?, middlename: String?, lastname: String?): List<Author>

    @Query("SELECT * FROM ${AuthorHelper.TABLE} WHERE authorid = :authorId")
    fun getById(authorId: String): List<Author?>

    @get:Query("SELECT SUBSTR(${AuthorHelper.COLUMN_FIRSTNAME}, 0, 1) as firstchar FROM ${AuthorHelper.TABLE} ORDER BY firstchar")
    val getAllGroupedAuthors: LiveData<List<String>>
}
