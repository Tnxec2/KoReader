package com.kontranik.koreader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kontranik.koreader.database.dao.BookStatusDao
import com.kontranik.koreader.database.dao.BookmarksDao
import com.kontranik.koreader.model.BookStatus
import com.kontranik.koreader.model.Bookmark
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// https://developer.android.com/codelabs/android-room-with-a-view#7

@Database(entities = [BookStatus::class, Bookmark::class],
    version = BooksRoomDatabase.SCHEMA,
    exportSchema = false)
abstract class BooksRoomDatabase : RoomDatabase() {
    abstract fun bookStatusDao(): BookStatusDao
    abstract fun bookmarksDao(): BookmarksDao

    companion object {
        @Volatile
        private var INSTANCE: BooksRoomDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(
            NUMBER_OF_THREADS
        )

        fun getDatabase(context: Context): BooksRoomDatabase {
            if (INSTANCE == null) {
                synchronized(BooksRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            BooksRoomDatabase::class.java,
                            DATABASE_NAME
                        )
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private const val DATABASE_NAME = "books.db" // db name
        internal const val SCHEMA = 5 // db version
    }
}
