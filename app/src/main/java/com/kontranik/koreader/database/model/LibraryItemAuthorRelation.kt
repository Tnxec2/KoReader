package com.kontranik.koreader.database.model

import androidx.room.*
import com.kontranik.koreader.model.BookInfo

@Entity(tableName = "libraryItemToAuthorCrossRef", primaryKeys = ["authorid", "libraryitemid"],
    indices = [Index(value = ["libraryitemid"])])
data class LibraryItemAuthorsCrossRef (
    val authorid: Long,
    val libraryitemid: Long
)

data class LibraryItemWithAuthors(
    @Embedded val libraryItem: LibraryItem,
    @Relation(
        parentColumn = "libraryitemid",
        entityColumn = "authorid",
        associateBy = Junction(LibraryItemAuthorsCrossRef::class)
    )
    val authors: List<Author>
) {
    @Ignore
    constructor(bookInfo: BookInfo) : this(
        libraryItem = LibraryItem(bookInfo),
        authors = bookInfo.authors ?: listOf()
    )
}

data class AuthorWithLibraryItems(
    @Embedded val author: Author,
    @Relation(
        parentColumn = "authorid",
        entityColumn = "libraryitemid",
        associateBy = Junction(LibraryItemAuthorsCrossRef::class)
    )
    val libraryItems: List<LibraryItem>
)