package com.kontranik.koreader.compose.ui.bookinfo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.repository.LibraryItemRepository
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.toBookInfoDetails
import com.kontranik.koreader.parser.EbookHelper
import kotlinx.coroutines.launch

class BookInfoViewModell(
    savedStateHandle: SavedStateHandle,
    private val libraryItemRepository: LibraryItemRepository,
    val context: Context,
) : ViewModel() {


    var bookInfoUiState = mutableStateOf(BookInfoUiState())
        private set

    var canDeleteState by mutableStateOf(false)
        private set

    var exit by mutableStateOf(false)

    private val source: String = savedStateHandle[BookInfoDestination.BOOK_PATH] ?: "preview"

    val bookPath = Uri.decode(source).replace('|','%')

    init {
        readBookInfo(bookPath)
    }

    private fun readBookInfo(bookPath: String?) {
        viewModelScope.launch {
            exit = false
            bookPath?.let { path ->
                val uri = Uri.parse(bookPath)
                val doc = DocumentFile.fromSingleUri(context, uri)

                canDeleteState = doc?.canWrite() == true

                val ebookHelper = EbookHelper.getHelper(path)

                if ( ebookHelper == null) exit = true

                ebookHelper?.let { helper ->
                    val bookInfo = helper.getBookInfoTemporary(path)
                    if ( bookInfo == null) exit = true

                    bookInfo?.let { info ->
                        bookInfoUiState.value = BookInfoUiState(info.toBookInfoDetails())
                    }
                }
            }
        }
    }

    fun readLibraryInfo() {
            KoReaderApplication.getApplicationScope().launch {
                val libraryItemWithAuthors =
                    libraryItemRepository.getByPathWithAuthors(bookInfoUiState.value.bookInfoDetails.path).firstOrNull()

                libraryItemWithAuthors?.let { item ->
                    bookInfoUiState.value =
                        BookInfoUiState(
                            item.toBookInfoDetails(
                                bookInfoUiState.value.bookInfoDetails.cover,
                                bookInfoUiState.value.bookInfoDetails.annotation
                            )
                        )
                }
            }

    }
}

data class BookInfoUiState(
    val bookInfoDetails: BookInfoDetails = BookInfoDetails()
)


data class BookInfoDetails(
    var title: String = "",
    var cover: Bitmap? = null,
    var authors: List<Author> = listOf(),
    val allAuthors: String = "",
    val filename: String = "",
    val path: String = "",
    var annotation: String = ""
)

fun BookInfo.toBookInfoDetails(): BookInfoDetails {
    return BookInfoDetails(
        title = title ?: "",
        cover = cover,
        authors = authors?.toList() ?: listOf(),
        allAuthors = authorsAsString(),
        filename = filename,
        path = path,
        annotation = annotation
    )
}