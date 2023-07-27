package com.kontranik.koreader.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.utils.ImageUtils


@Entity(tableName = LibraryItemHelper.TABLE)
class LibraryItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = LibraryItemHelper.COLUMN_ID)
    var id: Long? = null,

    @ColumnInfo(name = LibraryItemHelper.COLUMN_PATH)
    var path: String? = null,

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
}