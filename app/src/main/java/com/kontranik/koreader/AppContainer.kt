package com.kontranik.koreader

import android.content.Context
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.repository.AuthorsRepository
import com.kontranik.koreader.database.repository.BookStatusRepository
import com.kontranik.koreader.database.repository.BookmarksRepository
import com.kontranik.koreader.database.repository.LibraryItemRepository
import kotlinx.coroutines.CoroutineScope

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val authorsRepository: AuthorsRepository
    val bookmarksRepository: BookmarksRepository
    val bookStatusRepository: BookStatusRepository
    val libraryItemRepository: LibraryItemRepository
}


class AppDataContainer(private val context: Context, private val applicationScope: CoroutineScope) : AppContainer {

    private val database by lazy { BooksRoomDatabase.getDatabase(context, applicationScope) }
    override val bookmarksRepository by lazy { BookmarksRepository(database.bookmarksDao()) }
    override val bookStatusRepository by lazy { BookStatusRepository(database.bookStatusDao()) }
    override val libraryItemRepository by lazy { LibraryItemRepository(database.libraryItemDao()) }
    override val authorsRepository by lazy { AuthorsRepository(database.authorDao()) }

}
