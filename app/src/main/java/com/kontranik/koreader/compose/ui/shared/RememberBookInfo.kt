package com.kontranik.koreader.compose.ui.shared

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.BookStatus
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookInfoComposable
import com.kontranik.koreader.model.toBookInfoComposable
import com.kontranik.koreader.model.toBookInfoComposableForImageBitmap
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.ImageUtils.getBitmap
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@Composable
fun rememberBookInfoUiStateForPath(
    bookPath: String,
    bookInfoComposable: BookInfoComposable
    ): State<BookInfoUiState> {

    val context = LocalContext.current
    val init = BookInfoUiState(
        bookInfoComposable,
        canDelete = false,
        exit = false
    )
    return rememberAsyncState(
        key = bookPath,
        initialValue = init,
        fetcher = {
            var bookInfoState = init
            val uri = bookPath.toUri()
            val doc = DocumentFile.fromSingleUri(context, uri)

            val ebookHelper = EbookHelper.getHelper(bookPath)

            if (ebookHelper != null) {
                val bookInfo = ebookHelper.getBookInfoTemporary(bookPath)

                if (bookInfo != null) {
                    bookInfoState = BookInfoUiState(
                        bookInfo.toBookInfoComposable(),
                        canDelete = doc?.canRead() == true,
                        exit = false
                    )
                    KoReaderApplication.getApplicationScope().launch {
                        val libraryItemWithAuthors = KoReaderApplication.getContainer().
                        libraryItemRepository.getByPathWithAuthors(bookPath).firstOrNull()

                        libraryItemWithAuthors?.let { item ->
                            bookInfoState =
                                BookInfoUiState(
                                    item.toBookInfoComposableForImageBitmap(
                                        bookInfoState.bookInfoComposable.cover,
                                        bookInfoState.bookInfoComposable.annotation
                                    )
                                )
                        }
                    }
                } else {
                    bookInfoState = bookInfoState.copy(
                        exit = true
                    )
                }
            } else {
                bookInfoState = bookInfoState.copy(
                    exit = true
                )
            }
            bookInfoState
        }
    )
}


@Composable
fun rememberBookInfoForFileItem(fileItem: FileItem, onUpdateItem: (bookInfoComposable: BookInfoComposable) -> Unit): State<BookInfoComposable> {
    val context = LocalContext.current
    val init = fileItem.toBookInfoComposable(getBitmap(context, fileItem.image).asImageBitmap())
    return rememberAsyncState(
        key = fileItem,
        initialValue = init,
        fetcher = {
            var state = init
            if (fileItem.bookInfo == null && !fileItem.isDir) {
                try {
                    fileItem.uriString?.let { contentUriPath ->
                        EbookHelper.getBookInfo(contentUriPath, fileItem)?.let { bookInfo ->
                            state = bookInfo.toBookInfoComposable()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                onUpdateItem(state)
            }
            state
        }
    )
}

@Composable
fun rememberBookInfoForBookStatus(bookStatus: BookStatus, bookStatusViewModel: BookStatusViewModel?): State<BookInfoComposable> {
    val context = LocalContext.current
    val init = bookStatus.toBookInfoComposable(getBitmap(context, ImageEnum.Ebook).asImageBitmap())

    return rememberAsyncState(
        key = bookStatus,
        initialValue = init,
        fetcher = {
            var state = init
            if (bookStatus.cover == null) {
                val contentUriPath = bookStatus.path
                if (contentUriPath != null) {
                    if (!FileHelper.contentFileExist(context, bookStatus.path)) {
                        bookStatusViewModel?.delete(bookStatus.id!!)
                    } else {
                        val uri = contentUriPath.toUri()
                        val doc = DocumentFile.fromSingleUri(context, uri)
                        if (doc != null) {
                            val bookInfoTemp: BookInfo? =
                                EbookHelper.getBookInfoTemporary(contentUriPath)
                            bookInfoTemp?.let { bookInfo ->
                                state = state.copy(
                                    cover = bookInfo.cover?.asImageBitmap() ?: state.cover,
                                    authors = bookInfo.authors ?: mutableListOf(),
                                    authorsAsString = bookInfo.authorsAsString()
                                )
                            }
                        }
                    }
                }
            }
            state
        }
    )

}

@Composable
fun rememberBookInfoForLibraryItem(item: LibraryItemWithAuthors): State<BookInfoComposable> {
    val context = LocalContext.current

    val bookInfoState = remember {
        mutableStateOf(
            item.toBookInfoComposable(
                cover = if (item.libraryItem.cover != null) {
                    ImageUtils.getImage(item.libraryItem.cover!!) ?: getBitmap(
                        context,
                        ImageEnum.Ebook
                    )
                } else {
                    getBitmap(context, ImageEnum.Ebook)
                }
            )
        )
    }

    return bookInfoState
}

data class BookInfoUiState(
    val bookInfoComposable: BookInfoComposable = BookInfoComposable(),
    val canDelete: Boolean = false,
    val exit: Boolean = false
)


data class BookInfoDetails(
    var title: String = "",
    var cover: Bitmap? = null,
    var authors: List<Author> = listOf(),
    val allAuthors: String = "",
    val filename: String = "",
    val path: String = "",
    var annotation: String = "",
    var sequenceName: String = "",
    var sequenceNumber: String = "",
)

fun BookInfo.toBookInfoDetails(): BookInfoDetails {
    return BookInfoDetails(
        title = title ?: "",
        cover = cover,
        authors = authors?.toList() ?: listOf(),
        allAuthors = authorsAsString(),
        filename = filename,
        path = path,
        annotation = annotation,
        sequenceName = sequenceName ?: "",
        sequenceNumber = sequenceNumber ?: ""
    )
}