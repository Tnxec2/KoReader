package com.kontranik.koreader

import android.app.Application
import android.content.Context
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.repository.AuthorsRepository
import com.kontranik.koreader.database.repository.BookStatusRepository
import com.kontranik.koreader.database.repository.BookmarksRepository
import com.kontranik.koreader.database.repository.LibraryItemRepository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class App : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { BooksRoomDatabase.getDatabase(this, applicationScope) }
    val bookmarksRepository by lazy { BookmarksRepository(database.bookmarksDao()) }
    val bookStatusRepository by lazy { BookStatusRepository(database.bookStatusDao()) }
    val libraryItemRepository by lazy { LibraryItemRepository(database.libraryItemDao()) }
    val authorsRepository by lazy { AuthorsRepository(database.authorDao()) }


    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    companion object {
        lateinit var mInstance: App
        fun getContext(): Context {
            return mInstance.applicationContext
        }

        fun getApplicationScope(): CoroutineScope {
            return mInstance.applicationScope
        }
    }
}