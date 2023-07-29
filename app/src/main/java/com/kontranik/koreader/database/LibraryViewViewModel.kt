package com.kontranik.koreader.database

import androidx.lifecycle.ViewModel
import com.kontranik.koreader.database.repository.AuthorsRepository
import com.kontranik.koreader.database.repository.LibraryItemRepository

class LibraryViewModel(private val libraryItemRepository: LibraryItemRepository, private val authorsRepository: AuthorsRepository) : ViewModel() {

}