package com.kontranik.koreader.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.utils.ImageUtils


@Entity(tableName = LibraryItemHelper.TABLE)
class LibraryItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = LibraryItemHelper.COLUMN_ID)
    var id: Long? = null,

    @ColumnInfo(name = LibraryItemHelper.COLUMN_PATH)
    var path: String,

    @ColumnInfo(name = LibraryItemHelper.COLUMN_TITLE)
    var title: String? = null,

    @ColumnInfo(name = LibraryItemHelper.COLUMN_COVER, typeAffinity = ColumnInfo.BLOB)
    var cover: ByteArray? = null
) {
    constructor(book: Book) : this(
        id = null,
        path = book.fileLocation,
        title = book.ebookHelper?.bookInfo?.title,
        cover = ImageUtils.bitmapToByteArray(book.ebookHelper?.bookInfo?.cover)
    )

    constructor(bookInfo: BookInfo) :this(
        id = null,
        path = bookInfo.path,
        title = bookInfo.title,
        cover = ImageUtils.bitmapToByteArray(bookInfo.cover)
    )

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + title.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LibraryItem

        if (id != other.id) return false
        if (path != other.path) return false
        if (title != other.title) return false

        return true
    }
}