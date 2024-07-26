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
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.parser.EbookHelper
import kotlinx.coroutines.launch

class BookInfoViewModell(
    savedStateHandle: SavedStateHandle,
    context: Context,
) : ViewModel(), IBookInfoViewModell {

    override var bookInfoUiState by mutableStateOf(BookInfoUiState())
        private set

    override var canDeleteState by mutableStateOf(false)
        private set

    override var exit by mutableStateOf(false)

    override val bookPath: String? = savedStateHandle[BookInfoDestination.BOOK_PATH]

    init {
        viewModelScope.launch {
            exit = false
            bookPath?.let { path ->
                val uri = Uri.parse(bookPath)
                val doc = DocumentFile.fromSingleUri(context, uri)

                val canDeleteState = doc?.canWrite() == true

                val ebookHelper = EbookHelper.getHelper(context, path)

                if ( ebookHelper == null) exit = true

                ebookHelper?.let { helper ->
                    val bookInfo = helper.getBookInfoTemporary(path)
                    if ( bookInfo == null) exit = true

                    bookInfo?.let { info ->
                        bookInfoUiState = BookInfoUiState(info.toBookInfoDetails())
                    }
                }

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