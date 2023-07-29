package com.kontranik.koreader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kontranik.koreader.database.dao.AuthorDao
import com.kontranik.koreader.database.dao.BookStatusDao
import com.kontranik.koreader.database.dao.BookmarksDao
import com.kontranik.koreader.database.dao.LibraryItemDao
import com.kontranik.koreader.database.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// https://developer.android.com/codelabs/android-room-with-a-view#7

@Database(
    entities = [BookStatus::class, Bookmark::class, Author::class, LibraryItem::class, LibraryItemAuthorsCrossRef::class],
    version = BooksRoomDatabase.SCHEMA,
    exportSchema = true)
abstract class BooksRoomDatabase : RoomDatabase() {
    abstract fun bookStatusDao(): BookStatusDao
    abstract fun bookmarksDao(): BookmarksDao
    abstract fun libraryItemDao(): LibraryItemDao
    abstract fun authorDao(): AuthorDao

    private class BooksRoomDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    // code to prepopulate database content
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: BooksRoomDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(
            NUMBER_OF_THREADS
        )

        fun getDatabase(context: Context,
                        scope: CoroutineScope
        ): BooksRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BooksRoomDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(BooksRoomDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private const val DATABASE_NAME = "books.db" // db name
        internal const val SCHEMA = 1 // db version
    }
}
