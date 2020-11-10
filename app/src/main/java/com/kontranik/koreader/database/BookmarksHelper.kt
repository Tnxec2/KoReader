package com.kontranik.koreader.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookmarksHelper()  {

    companion object {
        const val TABLE = "Bookmarks" // название таблицы в бд

        // названия столбцов
        const val COLUMN_ID = "_id"
        const val COLUMN_PATH = "path"
        const val COLUMN_TEXT = "text"
        const val COLUMN_SORT = "sort"
        const val COLUMN_POSITION_PAGE = "position_page"
        const val COLUMN_POSITION_ELEMENT = "position_element"
        const val COLUMN_POSITION_PARAGRAPH = "position_paragraph"
        const val COLUMN_POSITION_SYMBOL = "position_symbol"
        const val COLUMN_CREATE_DATE = "create_date"
    }
}