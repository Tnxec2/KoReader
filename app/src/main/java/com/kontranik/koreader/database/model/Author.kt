package com.kontranik.koreader.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = AuthorHelper.TABLE)
class Author(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = AuthorHelper.COLUMN_ID)
    var id: Long? = null,

    @ColumnInfo(name = AuthorHelper.COLUMN_FIRSTNAME)
    var firstname: String? = null,

    @ColumnInfo(name = AuthorHelper.COLUMN_MIDDLENAME)
    var middlename: String? = null,

    @ColumnInfo(name = AuthorHelper.COLUMN_LASTNAME)
    var lastname: String? = null,

) {
    constructor(author: com.kontranik.koreader.model.Author) : this(
        id = null,
        firstname = author.firstname,
        middlename = author.middlename,
        lastname = author.lastname
    )
}