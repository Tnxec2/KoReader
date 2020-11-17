package com.kontranik.koreader.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import android.database.DatabaseUtils

import android.database.sqlite.SQLiteDatabase
import com.kontranik.koreader.model.BookStatus


class BookStatusDatabaseAdapter(val context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context.getApplicationContext())
    private var database: SQLiteDatabase? = null

    fun open(): BookStatusDatabaseAdapter {
        database = dbHelper.writableDatabase
        return this
    }

    fun close() {
        dbHelper.close()
    }

    private val allEntries: Cursor
        get() {
            val columns = arrayOf(
                    BookStatusHelper.COLUMN_ID,
                    BookStatusHelper.COLUMN_PATH,
                    BookStatusHelper.COLUMN_POSITION_PAGE,
                    BookStatusHelper.COLUMN_POSITION_OFFSET,
                    BookStatusHelper.COLUMN_LAST_OPEN_TIME
            )
            return database!!.query(
                    BookStatusHelper.TABLE, columns, null, null, null, null, null)
        }

    val allBookStatus: MutableList<BookStatus>
        get() {
            val books: MutableList<BookStatus> = mutableListOf()
            val cursor: Cursor = allEntries
            if (cursor.moveToFirst()) {
                do {
                    val id: Long = cursor.getLong(cursor.getColumnIndex(BookStatusHelper.COLUMN_ID))
                    val path: String = cursor.getString(cursor.getColumnIndex(BookStatusHelper.COLUMN_PATH))
                    val page: Int = cursor.getInt(cursor.getColumnIndex(BookStatusHelper.COLUMN_POSITION_PAGE))
                    val offset: Int = cursor.getInt(cursor.getColumnIndex(BookStatusHelper.COLUMN_POSITION_OFFSET))
                    val lastOpenDate: Long = cursor.getLong(cursor.getColumnIndex(BookStatusHelper.COLUMN_LAST_OPEN_TIME))
                    books.add(BookStatus(id, path, page, offset, lastOpenDate))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return books
        }

    val count: Long
        get() = DatabaseUtils.queryNumEntries(database, BookStatusHelper.TABLE)

    fun getLastOpened(count: Int): MutableList<BookStatus> {
        val result: MutableList<BookStatus> = mutableListOf()
        val query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT ?",
                BookStatusHelper.TABLE,
                BookStatusHelper.COLUMN_LAST_OPEN_TIME,
        )
        val cursor: Cursor = database!!.rawQuery(query, arrayOf(count.toString()))
        if (cursor.moveToFirst()) {
            do {
                val id: Long = cursor.getLong(cursor.getColumnIndex(BookStatusHelper.COLUMN_ID))
                val path: String = cursor.getString(cursor.getColumnIndex(BookStatusHelper.COLUMN_PATH))
                val page: Int = cursor.getInt(cursor.getColumnIndex(BookStatusHelper.COLUMN_POSITION_PAGE))
                val offset: Int = cursor.getInt(cursor.getColumnIndex(BookStatusHelper.COLUMN_POSITION_OFFSET))
                val lastOpenDate: Long = cursor.getLong(cursor.getColumnIndex(BookStatusHelper.COLUMN_LAST_OPEN_TIME))
                result.add(BookStatus(id, path, page, offset, lastOpenDate))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    fun getBookStatus(id: Long): BookStatus? {
        var bookStatus: BookStatus? = null
        val query = String.format("SELECT * FROM %s WHERE %s=?", BookStatusHelper.TABLE, BookStatusHelper.COLUMN_ID)
        val cursor: Cursor = database!!.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            val path: String = cursor.getString(cursor.getColumnIndex(BookStatusHelper.COLUMN_PATH))
            val page: Int = cursor.getInt(cursor.getColumnIndex(BookStatusHelper.COLUMN_POSITION_PAGE))
            val offset: Int = cursor.getInt(cursor.getColumnIndex(BookStatusHelper.COLUMN_POSITION_OFFSET))
            val lastOpenDate: Long = cursor.getLong(cursor.getColumnIndex(BookStatusHelper.COLUMN_LAST_OPEN_TIME))
            bookStatus = BookStatus(id, path, page, offset, lastOpenDate)
        }
        cursor.close()
        return bookStatus
    }

    fun getBookStatusByPath(inputPath: String): BookStatus? {
        var bookStatus: BookStatus? = null
        val query = String.format("SELECT * FROM %s WHERE %s=?", BookStatusHelper.TABLE,
                BookStatusHelper.COLUMN_PATH)
        val cursor: Cursor = database!!.rawQuery(query, arrayOf(inputPath))
        if (cursor.moveToFirst()) {
            val id: Long = cursor.getLong(cursor.getColumnIndex(BookStatusHelper.COLUMN_ID))
            val path: String = cursor.getString(cursor.getColumnIndex(BookStatusHelper.COLUMN_PATH))
            val page: Int = cursor.getInt(cursor.getColumnIndex(BookStatusHelper.COLUMN_POSITION_PAGE))
            val offset: Int = cursor.getInt(cursor.getColumnIndex(BookStatusHelper.COLUMN_POSITION_OFFSET))
            val lastOpenDate: Long = cursor.getLong(cursor.getColumnIndex(BookStatusHelper.COLUMN_LAST_OPEN_TIME))
            bookStatus = BookStatus(id, path, page, offset, lastOpenDate)
        }
        cursor.close()
        return bookStatus
    }

    fun insert(bookStatus: BookStatus): Long {
        val cv = ContentValues()
        cv.put(BookStatusHelper.COLUMN_PATH, bookStatus.path)
        cv.put(BookStatusHelper.COLUMN_POSITION_PAGE, bookStatus.position_section)
        cv.put(BookStatusHelper.COLUMN_POSITION_OFFSET, bookStatus.position_offset)
        cv.put(BookStatusHelper.COLUMN_LAST_OPEN_TIME, bookStatus.lastOpenTime)
        return database!!.insert(BookStatusHelper.TABLE, null, cv)
    }

    fun delete(userId: Long): Long {
        val whereClause = "_id = ?"
        val whereArgs = arrayOf(userId.toString())
        return database!!.delete(BookStatusHelper.TABLE, whereClause, whereArgs).toLong()
    }

    fun update(bookStatus: BookStatus): Long {
        val whereClause = "${BookStatusHelper.COLUMN_ID} = ${bookStatus.id}"
        val cv = ContentValues()
        cv.put(BookStatusHelper.COLUMN_PATH, bookStatus.path)
        cv.put(BookStatusHelper.COLUMN_POSITION_PAGE, bookStatus.position_section)
        cv.put(BookStatusHelper.COLUMN_POSITION_OFFSET, bookStatus.position_offset)
        cv.put(BookStatusHelper.COLUMN_LAST_OPEN_TIME, bookStatus.lastOpenTime)
        return database!!.update(BookStatusHelper.TABLE, cv, whereClause, null).toLong()
    }

}