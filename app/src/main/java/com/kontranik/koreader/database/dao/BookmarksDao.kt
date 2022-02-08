package com.kontranik.koreader.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kontranik.koreader.database.BookmarksHelper
import com.kontranik.koreader.model.Bookmark


@Dao
interface BookmarksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bookmark: Bookmark)

    @Update
    fun update(bookStatus: Bookmark)

    @Query("DELETE FROM ${BookmarksHelper.TABLE} where ${BookmarksHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @Query("SELECT * FROM ${BookmarksHelper.TABLE} where ${BookmarksHelper.COLUMN_PATH} = :path ORDER BY ${BookmarksHelper.COLUMN_POSITION_PAGE} ASC, ${BookmarksHelper.COLUMN_POSITION_OFFSET} ASC")
    fun getByPath(path: String): LiveData<List<Bookmark>>

    @get:Query("SELECT * FROM ${BookmarksHelper.TABLE}")
    val getAll: LiveData<List<Bookmark>>


}
