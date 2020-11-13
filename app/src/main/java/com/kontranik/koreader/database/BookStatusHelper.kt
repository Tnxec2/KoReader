package com.kontranik.koreader.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookStatusHelper() {

    companion object {
        const val TABLE = "Books" // название таблицы в бд

        // названия столбцов
        const val COLUMN_ID = "_id"
        const val COLUMN_PATH = "path"
        //const val COLUMN_TITLE = "title"
        //const val COLUMN_AUTHOR = "author"
        const val COLUMN_POSITION_PAGE = "position_page"
        const val COLUMN_POSITION_OFFSET = "position_offset"
        const val COLUMN_LAST_OPEN_TIME = "last_open_time"
    }
}