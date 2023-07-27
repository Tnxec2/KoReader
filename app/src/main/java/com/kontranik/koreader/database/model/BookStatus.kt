package com.kontranik.koreader.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kontranik.koreader.database.BookStatusHelper
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPosition
import java.util.*

@Entity(tableName = BookStatusHelper.TABLE)
class BookStatus(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BookStatusHelper.COLUMN_ID) var id: Long? = null,
    @ColumnInfo(name = BookStatusHelper.COLUMN_PATH) var path: String? = null,
    @ColumnInfo(name = BookStatusHelper.COLUMN_TITLE) var title: String? = null,
    @ColumnInfo(name = BookStatusHelper.COLUMN_AUTHOR) var authors: String? = null,
    @ColumnInfo(name = BookStatusHelper.COLUMN_POSITION_PAGE) var position_section: Int = 0,
    @ColumnInfo(name = BookStatusHelper.COLUMN_POSITION_OFFSET) var position_offset: Int = 0,
    @ColumnInfo(name = BookStatusHelper.COLUMN_LAST_OPEN_TIME) var lastOpenTime: Long? = null
) {
    constructor(book: Book) : this(
        id = null,
        path = book.fileLocation,
        title = book.ebookHelper?.bookInfo?.title,
        authors = book.ebookHelper?.bookInfo?.authorsAsString(),
        position_section = if ( book.curPage == null) 0 else book.curPage!!.startBookPosition.section,
        position_offset = if ( book.curPage == null) 0 else book.curPage!!.startBookPosition.offSet
    )

    init {
        if ( lastOpenTime == null ) updateLastOpenTime()
    }

    fun updatePosition(p: BookPosition) {
        position_section = p.section
        position_offset = p.offSet
    }

    fun updateLastOpenTime() {
        lastOpenTime = Date().time
    }
}