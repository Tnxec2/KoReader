package com.kontranik.koreader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private const val DATABASE_NAME = "books.db" // db name
        internal const val SCHEMA = 3 // db version
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ${BookStatusHelper.TABLE} ADD COLUMN ${BookStatusHelper.SEQUENCE_NAME} TEXT")
        database.execSQL("ALTER TABLE ${BookStatusHelper.TABLE} ADD COLUMN ${BookStatusHelper.SEQUENCE_NUMBER} TEXT")
    }
}
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ${LibraryItemHelper.TABLE} ADD COLUMN ${LibraryItemHelper.SEQUENCE_NAME} TEXT")
        database.execSQL("ALTER TABLE ${LibraryItemHelper.TABLE} ADD COLUMN ${LibraryItemHelper.SEQUENCE_NUMBER} TEXT")
    }
}

