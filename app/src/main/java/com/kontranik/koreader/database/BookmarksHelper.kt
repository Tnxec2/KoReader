package com.kontranik.koreader.database

class BookmarksHelper()  {

    companion object {
        const val TABLE = "Bookmarks"

        const val COLUMN_ID = "_id"
        const val COLUMN_PATH = "path"
        const val COLUMN_TEXT = "text"
        const val COLUMN_SORT = "sort"
        const val COLUMN_POSITION_PAGE = "position_page"
        const val COLUMN_POSITION_OFFSET = "position_offset"
        const val COLUMN_CREATE_DATE = "create_date"
    }
}