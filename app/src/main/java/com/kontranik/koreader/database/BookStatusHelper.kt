package com.kontranik.koreader.database

class BookStatusHelper() {

    companion object {
        const val TABLE = "Books"
        const val COLUMN_ID = "_id"
        const val COLUMN_PATH = "path"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUTHOR = "author"
        const val SEQUENCE_NAME = "sequence_name"
        const val SEQUENCE_NUMBER = "sequence_number"
        const val COLUMN_POSITION_PAGE = "position_page"
        const val COLUMN_POSITION_OFFSET = "position_offset"
        const val COLUMN_LAST_OPEN_TIME = "last_open_time"
    }
}