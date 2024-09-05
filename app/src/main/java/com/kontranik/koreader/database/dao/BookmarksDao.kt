package com.kontranik.koreader.database.dao

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.room.*
import com.kontranik.koreader.database.BookmarksHelper
import com.kontranik.koreader.database.model.Bookmark
import kotlinx.coroutines.flow.Flow


@Dao
interface BookmarksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bookmark: Bookmark)

    @Update
    fun update(bookStatus: Bookmark)

    @Query("DELETE FROM ${BookmarksHelper.TABLE} where ${BookmarksHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @Query("SELECT * FROM ${BookmarksHelper.TABLE} where ${BookmarksHelper.COLUMN_PATH} = :path ORDER BY ${BookmarksHelper.COLUMN_POSITION_PAGE} ASC, ${BookmarksHelper.COLUMN_POSITION_OFFSET} ASC")
    fun getByPath(path: String): Flow<List<Bookmark>>

    @get:Query("SELECT * FROM ${BookmarksHelper.TABLE}")
    val getAll: LiveData<List<Bookmark>>

    @Query("SELECT * FROM ${BookmarksHelper.TABLE} " +
            "where ${BookmarksHelper.COLUMN_PATH} = :path " +
            "and ${BookmarksHelper.COLUMN_POSITION_PAGE} = :page " +
            "and ${BookmarksHelper.COLUMN_POSITION_OFFSET} >= :startOffset " +
            "and ${BookmarksHelper.COLUMN_POSITION_OFFSET} <= :endOffset " +
            "ORDER BY ${BookmarksHelper.COLUMN_POSITION_PAGE} ASC, ${BookmarksHelper.COLUMN_POSITION_OFFSET} ASC")
    fun getByPathAndPosition(path: String, page: Int, startOffset: Int, endOffset: Int): Flow<List<Bookmark>>

}
