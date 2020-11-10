package com.kontranik.koreader.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import android.database.DatabaseUtils

import android.database.sqlite.SQLiteDatabase
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.Bookmark

class BookmarksDatabaseAdapter(val context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context.getApplicationContext())
    private var database: SQLiteDatabase? = null

    fun open(): BookmarksDatabaseAdapter {
        database = dbHelper.writableDatabase
        return this
    }

    fun close() {
        dbHelper.close()
    }

    private val allEntries: Cursor
        get() {
            val columns = arrayOf(
                    BookmarksHelper.COLUMN_ID,
                    BookmarksHelper.COLUMN_PATH,
                    BookmarksHelper.COLUMN_TEXT,
                    BookmarksHelper.COLUMN_SORT,
                    BookmarksHelper.COLUMN_POSITION_PAGE,
                    BookmarksHelper.COLUMN_POSITION_ELEMENT,
                    BookmarksHelper.COLUMN_POSITION_PARAGRAPH,
                    BookmarksHelper.COLUMN_POSITION_SYMBOL,
                    BookmarksHelper.COLUMN_CREATE_DATE
            )
            return database!!.query(
                    BookmarksHelper.TABLE, columns, null, null, null, null, null)
        }

    val allBookmarks: MutableList<Bookmark>
        get() {
            val bookmarks: MutableList<Bookmark> = mutableListOf()
            val cursor: Cursor = allEntries
            if (cursor.moveToFirst()) {
                do {
                    val id: Long = cursor.getLong(cursor.getColumnIndex(BookmarksHelper.COLUMN_ID))
                    val path: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_PATH))
                    val text: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_TEXT))
                    val sort: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_SORT))
                    val page: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_PAGE))
                    val element: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_ELEMENT))
                    val paragraph: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_PARAGRAPH))
                    val symbol: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_SYMBOL))
                    val createDate: Long = cursor.getLong(cursor.getColumnIndex(BookmarksHelper.COLUMN_CREATE_DATE))
                    bookmarks.add(Bookmark(id, path = path, text = text, sort = sort, page, element, paragraph, symbol, createDate))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return bookmarks
        }

    val count: Long
        get() = DatabaseUtils.queryNumEntries(database, BookmarksHelper.TABLE)

    fun getBookmark(id: Long): Bookmark? {
        var bookmark: Bookmark? = null
        val query = String.format("SELECT * FROM %s WHERE %s=?", BookmarksHelper.TABLE, BookmarksHelper.COLUMN_ID)
        val cursor: Cursor = database!!.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            val path: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_PATH))
            val text: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_TEXT))
            val sort: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_SORT))
            val page: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_PAGE))
            val element: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_ELEMENT))
            val paragraph: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_PARAGRAPH))
            val symbol: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_SYMBOL))
            val createDate: Long = cursor.getLong(cursor.getColumnIndex(BookmarksHelper.COLUMN_CREATE_DATE))
            bookmark = Bookmark(id, path = path, text = text, sort = sort, page, element, paragraph, symbol, createDate)
        }
        cursor.close()
        return bookmark
    }

    fun getBookmarskByPath(inputPath: String): List<Bookmark> {
        val query = String.format("SELECT * FROM %s WHERE %s=? ORDER BY %s ASC, %s ASC, %s ASC, %s ASC, %s DESC ", BookmarksHelper.TABLE,
                BookmarksHelper.COLUMN_PATH,
                BookmarksHelper.COLUMN_POSITION_PAGE,
                BookmarksHelper.COLUMN_POSITION_ELEMENT,
                BookmarksHelper.COLUMN_POSITION_PARAGRAPH,
                BookmarksHelper.COLUMN_POSITION_SYMBOL,
                BookmarksHelper.COLUMN_CREATE_DATE
        )
        val bookmarks: MutableList<Bookmark> = mutableListOf()
        val cursor: Cursor = database!!.rawQuery(query, arrayOf(inputPath))
        if (cursor.moveToFirst()) {
            do {
                val id: Long = cursor.getLong(cursor.getColumnIndex(BookmarksHelper.COLUMN_ID))
                val path: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_PATH))
                val text: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_TEXT))
                val sort: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_SORT))
                val page: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_PAGE))
                val element: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_ELEMENT))
                val paragraph: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_PARAGRAPH))
                val symbol: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_SYMBOL))
                val createDate: Long = cursor.getLong(cursor.getColumnIndex(BookmarksHelper.COLUMN_CREATE_DATE))
                bookmarks.add(Bookmark(id, path = path, text = text, sort = sort, page, element, paragraph, symbol, createDate))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bookmarks
    }

    fun getBookmarskByPathAndPosition(inputPath: String, position: BookPosition): List<Bookmark> {
        val query = String.format("SELECT * FROM %s WHERE %s=? AND %s=? AND %s=? AND %s=? AND %s=? ORDER BY %s",
                BookmarksHelper.TABLE,
                BookmarksHelper.COLUMN_PATH,
                BookStatusHelper.COLUMN_POSITION_PAGE,
                BookStatusHelper.COLUMN_POSITION_ELEMENT,
                BookStatusHelper.COLUMN_POSITION_PARAGRAPH,
                BookStatusHelper.COLUMN_POSITION_SYMBOL,
                BookmarksHelper.COLUMN_SORT)
        val bookmarks: MutableList<Bookmark> = mutableListOf()
        val cursor: Cursor = database!!.rawQuery(query,
                arrayOf(inputPath, position.page.toString(), position.element.toString(), position.paragraph.toString(), position.symbol.toString()))
        if (cursor.moveToFirst()) {
            do {
                val id: Long = cursor.getLong(cursor.getColumnIndex(BookmarksHelper.COLUMN_ID))
                val path: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_PATH))
                val text: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_TEXT))
                val sort: String = cursor.getString(cursor.getColumnIndex(BookmarksHelper.COLUMN_SORT))
                val page: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_PAGE))
                val element: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_ELEMENT))
                val paragraph: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_PARAGRAPH))
                val symbol: Int = cursor.getInt(cursor.getColumnIndex(BookmarksHelper.COLUMN_POSITION_SYMBOL))
                val createDate: Long = cursor.getLong(cursor.getColumnIndex(BookmarksHelper.COLUMN_CREATE_DATE))
                bookmarks.add(Bookmark(id, path = path, text = text, sort = sort, page, element, paragraph, symbol, createDate))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bookmarks
    }

    fun insert(bookmark: Bookmark): Long {
        val cv = ContentValues()
        cv.put(BookmarksHelper.COLUMN_PATH, bookmark.path)
        cv.put(BookmarksHelper.COLUMN_TEXT, bookmark.text)
        cv.put(BookmarksHelper.COLUMN_SORT, bookmark.sort)
        cv.put(BookmarksHelper.COLUMN_POSITION_PAGE, bookmark.position_page)
        cv.put(BookmarksHelper.COLUMN_POSITION_ELEMENT, bookmark.position_element)
        cv.put(BookmarksHelper.COLUMN_POSITION_PARAGRAPH, bookmark.position_paragraph)
        cv.put(BookmarksHelper.COLUMN_POSITION_SYMBOL, bookmark.position_symbol)
        cv.put(BookmarksHelper.COLUMN_CREATE_DATE, bookmark.createDate)
        return database!!.insert(BookmarksHelper.TABLE, null, cv)
    }

    fun delete(userId: Long): Long {
        val whereClause = "_id = ?"
        val whereArgs = arrayOf(userId.toString())
        return database!!.delete(BookmarksHelper.TABLE, whereClause, whereArgs).toLong()
    }

    fun update(bookmark: Bookmark): Long {
        val whereClause = "${BookmarksHelper.COLUMN_ID} = ${bookmark.id}"
        val cv = ContentValues()
        cv.put(BookmarksHelper.COLUMN_PATH, bookmark.path)
        cv.put(BookmarksHelper.COLUMN_TEXT, bookmark.text)
        cv.put(BookmarksHelper.COLUMN_SORT, bookmark.sort)
        cv.put(BookmarksHelper.COLUMN_POSITION_PAGE, bookmark.position_page)
        cv.put(BookmarksHelper.COLUMN_POSITION_ELEMENT, bookmark.position_element)
        cv.put(BookmarksHelper.COLUMN_POSITION_PARAGRAPH, bookmark.position_paragraph)
        cv.put(BookmarksHelper.COLUMN_POSITION_SYMBOL, bookmark.position_symbol)
        cv.put(BookmarksHelper.COLUMN_CREATE_DATE, bookmark.createDate)
        return database!!.update(BookmarksHelper.TABLE, cv, whereClause, null).toLong()
    }

}