package com.kontranik.koreader.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kontranik.koreader.database.BookmarksHelper
import java.util.*

@Entity(tableName = BookmarksHelper.TABLE)
class Bookmark(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BookmarksHelper.COLUMN_ID) var id: Long? = null,
    @ColumnInfo(name = BookmarksHelper.COLUMN_PATH) var path: String,
    @ColumnInfo(name = BookmarksHelper.COLUMN_TEXT) var text: String? = null,
    @ColumnInfo(name = BookmarksHelper.COLUMN_SORT) var sort: String? = null,
    @ColumnInfo(name = BookmarksHelper.COLUMN_POSITION_PAGE) var position_section: Int = 0,
    @ColumnInfo(name = BookmarksHelper.COLUMN_POSITION_OFFSET) var position_offset: Int = 0,
    @ColumnInfo(name = BookmarksHelper.COLUMN_CREATE_DATE) var createDate: Long? = null
) {

    init {
        if ( createDate == null) createDate = Date().time
        if ( sort == null ) sort = "" + position_section + SORT_TRIM + position_offset + SORT_TRIM + createDate
    }

    companion object {
        const val SORT_TRIM = ":"
    }
}