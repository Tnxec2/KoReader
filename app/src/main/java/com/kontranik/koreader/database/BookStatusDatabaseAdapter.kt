package com.kontranik.koreader.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import android.database.DatabaseUtils

import android.database.sqlite.SQLiteDatabase
import com.kontranik.koreader.model.BookStatus


class BookStatusDatabaseAdapter(val context: Context) {

    private val dbHelperBookStatus: BookStatusDatabaseHelper = BookStatusDatabaseHelper(context.getApplicationContext())
    private var database: SQLiteDatabase? = null

    fun open(): BookStatusDatabaseAdapter {
        database = dbHelperBookStatus.writableDatabase
        return this
    }

    fun close() {
        dbHelperBookStatus.close()
    }

    private val allEntries: Cursor
        get() {
            val columns = arrayOf(
                    BookStatusDatabaseHelper.COLUMN_ID,
                    BookStatusDatabaseHelper.COLUMN_PATH,
                    BookStatusDatabaseHelper.COLUMN_POSITION_PAGE,
                    BookStatusDatabaseHelper.COLUMN_POSITION_ELEMENT,
                    BookStatusDatabaseHelper.COLUMN_POSITION_PARAGRAPH,
                    BookStatusDatabaseHelper.COLUMN_POSITION_SYMBOL
            )
            return database!!.query(
                    BookStatusDatabaseHelper.TABLE, columns, null, null, null, null, null)
        }

    val allBookStatus: MutableList<BookStatus>
        get() {
            val books: MutableList<BookStatus> = mutableListOf()
            val cursor: Cursor = allEntries
            if (cursor.moveToFirst()) {
                do {
                    val id: Long = cursor.getLong(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_ID))
                    val path: String = cursor.getString(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_PATH))
                    val page: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_PAGE))
                    val element: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_ELEMENT))
                    val paragraph: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_PARAGRAPH))
                    val symbol: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_SYMBOL))
                    books.add(BookStatus(id, path, page, element, paragraph, symbol))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return books
        }

    val count: Long
        get() = DatabaseUtils.queryNumEntries(database, BookStatusDatabaseHelper.TABLE)

    fun getBookStatus(id: Long): BookStatus? {
        var bookStatus: BookStatus? = null
        val query = String.format("SELECT * FROM %s WHERE %s=?", BookStatusDatabaseHelper.TABLE, BookStatusDatabaseHelper.COLUMN_ID)
        val cursor: Cursor = database!!.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            val path: String = cursor.getString(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_PATH))
            val page: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_PAGE))
            val element: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_ELEMENT))
            val paragraph: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_PARAGRAPH))
            val symbol: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_SYMBOL))
            bookStatus = BookStatus(id, path, page, element, paragraph, symbol)
        }
        cursor.close()
        return bookStatus
    }

    fun getBookStatusByPath(inputPath: String): BookStatus? {
        var bookStatus: BookStatus? = null
        val query = String.format("SELECT * FROM %s WHERE %s=?", BookStatusDatabaseHelper.TABLE,
                BookStatusDatabaseHelper.COLUMN_PATH)
        val cursor: Cursor = database!!.rawQuery(query, arrayOf(inputPath))
        if (cursor.moveToFirst()) {
            val id: Long = cursor.getLong(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_ID))
            val path: String = cursor.getString(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_PATH))
            val page: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_PAGE))
            val element: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_ELEMENT))
            val paragraph: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_PARAGRAPH))
            val symbol: Int = cursor.getInt(cursor.getColumnIndex(BookStatusDatabaseHelper.COLUMN_POSITION_SYMBOL))
            bookStatus = BookStatus(id, path, page, element, paragraph, symbol)
        }
        cursor.close()
        return bookStatus
    }

    fun insert(bookStatus: BookStatus): Long {
        val cv = ContentValues()
        cv.put(BookStatusDatabaseHelper.COLUMN_PATH, bookStatus.path)
        cv.put(BookStatusDatabaseHelper.COLUMN_POSITION_PAGE, bookStatus.position_page)
        cv.put(BookStatusDatabaseHelper.COLUMN_POSITION_ELEMENT, bookStatus.position_element)
        cv.put(BookStatusDatabaseHelper.COLUMN_POSITION_PARAGRAPH, bookStatus.position_paragraph)
        cv.put(BookStatusDatabaseHelper.COLUMN_POSITION_SYMBOL, bookStatus.position_symbol)
        return database!!.insert(BookStatusDatabaseHelper.TABLE, null, cv)
    }

    fun delete(userId: Long): Long {
        val whereClause = "_id = ?"
        val whereArgs = arrayOf(userId.toString())
        return database!!.delete(BookStatusDatabaseHelper.TABLE, whereClause, whereArgs).toLong()
    }

    fun update(bookStatus: BookStatus): Long {
        val whereClause = "${BookStatusDatabaseHelper.COLUMN_ID} = ${bookStatus.id}"
        val cv = ContentValues()
        cv.put(BookStatusDatabaseHelper.COLUMN_PATH, bookStatus.path)
        cv.put(BookStatusDatabaseHelper.COLUMN_POSITION_PAGE, bookStatus.position_page)
        cv.put(BookStatusDatabaseHelper.COLUMN_POSITION_ELEMENT, bookStatus.position_element)
        cv.put(BookStatusDatabaseHelper.COLUMN_POSITION_PARAGRAPH, bookStatus.position_paragraph)
        cv.put(BookStatusDatabaseHelper.COLUMN_POSITION_SYMBOL, bookStatus.position_symbol)
        return database!!.update(BookStatusDatabaseHelper.TABLE, cv, whereClause, null).toLong()
    }

}