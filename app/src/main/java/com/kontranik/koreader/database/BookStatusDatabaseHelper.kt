package com.kontranik.koreader.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookStatusDatabaseHelper(val context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, SCHEMA) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE " + TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PATH + " TEXT, "
                + COLUMN_POSITION_PAGE + " NUMBER, "
                + COLUMN_POSITION_ELEMENT + " NUMBER, "
                + COLUMN_POSITION_PARAGRAPH + " NUMBER, "
                + COLUMN_POSITION_SYMBOL + " NUMBER "

                + ");")
        // добавление начальных данных
        //db.execSQL("INSERT INTO " + TABLE + " (" + COLUMN_NAME
        //        + ", " + COLUMN_YEAR + ") VALUES ('Том Смит', 1981);")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE)
        //onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "books.db" // название бд
        private const val SCHEMA = 1 // версия базы данных
        const val TABLE = "Books" // название таблицы в бд

        // названия столбцов
        const val COLUMN_ID = "_id"
        const val COLUMN_PATH = "path"
        //const val COLUMN_TITLE = "title"
        //const val COLUMN_AUTHOR = "author"
        const val COLUMN_POSITION_PAGE = "position_page"
        const val COLUMN_POSITION_ELEMENT = "position_element"
        const val COLUMN_POSITION_PARAGRAPH = "position_paragraph"
        const val COLUMN_POSITION_SYMBOL = "position_symbol"
    }
}