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

class KoReaderApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    init {
        mInstance = this
    }

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this, applicationScope)
    }

    companion object {
        lateinit var mInstance: KoReaderApplication
        fun getContext(): Context {
            return mInstance.applicationContext
        }

        fun getApplicationScope(): CoroutineScope {
            return mInstance.applicationScope
        }
    }
}