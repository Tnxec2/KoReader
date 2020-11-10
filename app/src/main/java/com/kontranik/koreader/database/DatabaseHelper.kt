package com.kontranik.koreader.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(val context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, SCHEMA) {
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("CREATE TABLE " + BookStatusHelper.TABLE + " (" + BookStatusHelper.COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookStatusHelper.COLUMN_PATH + " TEXT, "
                + BookStatusHelper.COLUMN_POSITION_PAGE + " NUMBER, "
                + BookStatusHelper.COLUMN_POSITION_ELEMENT + " NUMBER, "
                + BookStatusHelper.COLUMN_POSITION_PARAGRAPH + " NUMBER, "
                + BookStatusHelper.COLUMN_POSITION_SYMBOL + " NUMBER, "
                + BookStatusHelper.COLUMN_LAST_OPEN_TIME + " NUMBER "

                + ");")

        db.execSQL("CREATE TABLE " + BookmarksHelper.TABLE + " (" + BookmarksHelper.COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookmarksHelper.COLUMN_PATH + " TEXT, "
                + BookmarksHelper.COLUMN_TEXT + " TEXT, "
                + BookmarksHelper.COLUMN_SORT + " TEXT, "
                + BookmarksHelper.COLUMN_POSITION_PAGE + " NUMBER, "
                + BookmarksHelper.COLUMN_POSITION_ELEMENT + " NUMBER, "
                + BookmarksHelper.COLUMN_POSITION_PARAGRAPH + " NUMBER, "
                + BookmarksHelper.COLUMN_POSITION_SYMBOL + " NUMBER , "
                + BookmarksHelper.COLUMN_CREATE_DATE + " NUMBER "
                + ");")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        if ( oldVersion < 2) {
            db.execSQL("ALTER TABLE " + BookmarksHelper.TABLE + " ADD COLUMN " +
                    BookmarksHelper.COLUMN_CREATE_DATE + " NUMBER;")
            db.execSQL("ALTER TABLE " + BookStatusHelper.TABLE + " ADD COLUMN " +
                    BookStatusHelper.COLUMN_LAST_OPEN_TIME + " NUMBER;")
        }
    }


    companion object {
        private const val DATABASE_NAME = "books.db" // db name
        private const val SCHEMA = 2 // db version
    }
}