package com.kontranik.koreader.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.kontranik.koreader.database.BookStatusHelper
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.Page
import com.kontranik.koreader.utils.ImageUtils
import java.util.*

@Entity(tableName = BookStatusHelper.TABLE)
class BookStatus(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BookStatusHelper.COLUMN_ID) var id: Long? = null,
    @ColumnInfo(name = BookStatusHelper.COLUMN_PATH) var path: String? = null,
    @ColumnInfo(name = BookStatusHelper.COLUMN_TITLE) var title: String? = null,
    @ColumnInfo(name = BookStatusHelper.COLUMN_AUTHOR) var authors: String? = null,
    @ColumnInfo(name = BookStatusHelper.SEQUENCE_NAME) var sequenceName: String? = null,
    @ColumnInfo(name = BookStatusHelper.SEQUENCE_NUMBER) var sequenceNumber: String? = null,
    @ColumnInfo(name = BookStatusHelper.COLUMN_POSITION_PAGE) var position_section: Int = 0,
    @ColumnInfo(name = BookStatusHelper.COLUMN_POSITION_OFFSET) var position_offset: Int = 0,
    @ColumnInfo(name = BookStatusHelper.COLUMN_LAST_OPEN_TIME) var lastOpenTime: Long? = null,
    @ColumnInfo(name = LibraryItemHelper.COLUMN_COVER, typeAffinity = ColumnInfo.BLOB)
    var cover: ByteArray? = null
) {

    @Ignore
    constructor(book: Book, curPage: Page) : this(
        id = null,
        path = book.fileLocation,
        title = book.ebookHelper?.bookInfo?.title,
        authors = book.ebookHelper?.bookInfo?.authorsAsString(),
        position_section = curPage.pageStartPosition.section,
        position_offset = curPage.pageStartPosition.offSet,
        cover = ImageUtils.getBytes(book.ebookHelper?.bookInfo?.cover),
        sequenceName = book.ebookHelper?.bookInfo?.sequenceName,
        sequenceNumber = book.ebookHelper?.bookInfo?.sequenceNumber
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