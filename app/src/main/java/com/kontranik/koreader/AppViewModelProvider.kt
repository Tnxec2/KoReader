package com.kontranik.koreader

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoViewModell
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.BookmarksViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            ReaderActivityViewModel(
                koReaderApplication().container.bookStatusRepository
            )
        }

        initializer {
            BookmarksViewModel(
                koReaderApplication().container.bookmarksRepository
            )
        }

        initializer {
            BookStatusViewModel(
                koReaderApplication().container.bookStatusRepository
            )
        }

        initializer {
            BookInfoViewModell(
                this.createSavedStateHandle(),
                koReaderApplication().applicationContext
            )
        }

    }
}

/**
 * Extension function to queries for [KoReaderApplication] object and returns an instance of
 * [KoReaderApplication].
 */
fun CreationExtras.koReaderApplication(): KoReaderApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as KoReaderApplication)