package com.kontranik.koreader

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoViewModell
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.BookmarksViewModel
import com.kontranik.koreader.compose.ui.library.LibraryViewModel
import com.kontranik.koreader.compose.ui.opds.OpdsViewModell
import com.kontranik.koreader.compose.ui.openfile.OpenFileViewModel
import com.kontranik.koreader.compose.ui.reader.BookReaderViewModel
import com.kontranik.koreader.compose.ui.settings.ColorThemeSettingsViewModel
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            BookReaderViewModel(
                koReaderApplication().container.bookStatusRepository,
                koReaderApplication().container.bookmarksRepository,
            )
        }

        initializer {
            BookmarksViewModel(
                this.createSavedStateHandle(),
                koReaderApplication().container.bookmarksRepository
            )
        }

        initializer {
            BookStatusViewModel(
                koReaderApplication().container.bookStatusRepository,
                koReaderApplication().container.libraryItemRepository,
                koReaderApplication().container.authorsRepository,
            )
        }

        initializer {
            BookInfoViewModell(
                this.createSavedStateHandle(),
                koReaderApplication().container.libraryItemRepository,
                koReaderApplication().applicationContext
            )
        }

        initializer {
            ColorThemeSettingsViewModel(
                this.createSavedStateHandle()
            )
        }

        initializer {
            OpdsViewModell(
                this.createSavedStateHandle(),
                koReaderApplication().applicationContext
            )
        }

        initializer {
            OpenFileViewModel()
        }

        initializer {
            SettingsViewModel(
                koReaderApplication().applicationContext
            )
        }

        initializer {
            LibraryViewModel(
                this.createSavedStateHandle(),
                koReaderApplication().container.libraryItemRepository,
                koReaderApplication().container.authorsRepository,
                koReaderApplication().applicationScope,
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
